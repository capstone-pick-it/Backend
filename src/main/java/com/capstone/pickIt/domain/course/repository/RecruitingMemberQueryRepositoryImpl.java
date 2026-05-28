package com.capstone.pickIt.domain.course.repository;

import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSort;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberItemResponseDTO;
import com.capstone.pickIt.domain.course.entity.ImportanceLevel;
import com.capstone.pickIt.domain.course.entity.QUserCourseProfile;
import com.capstone.pickIt.domain.course.entity.QUserCourseTrait;
import com.capstone.pickIt.domain.course.entity.RecruitmentStatus;
import com.capstone.pickIt.domain.matching.entity.QMatchScore;
import com.capstone.pickIt.domain.point.entity.QPoint;
import com.capstone.pickIt.domain.project.entity.QProjectTeamMember;
import com.capstone.pickIt.domain.trait.entity.QTraitItem;
import com.capstone.pickIt.domain.trait.entity.TraitSide;
import com.capstone.pickIt.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecruitingMemberQueryRepositoryImpl implements RecruitingMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RecruitingMemberItemResponseDTO> searchRecruitingMembers(
            Long courseId,
            Long currentUserId,
            String keyword,
            RecruitingMemberSort sort,
            List<String> traits,
            boolean includeCompleted,
            int page,
            int size
    ) {
        QUserCourseProfile profile = QUserCourseProfile.userCourseProfile;
        QUser user = QUser.user;
        QPoint point = QPoint.point;
        QProjectTeamMember member = QProjectTeamMember.projectTeamMember;
        QMatchScore matchScore = QMatchScore.matchScore;

        Pageable pageable = PageRequest.of(page, size);

        BooleanBuilder condition = baseCondition(
                profile,
                user,
                courseId,
                currentUserId,
                keyword,
                includeCompleted
        );

        List<Long> profileIds = queryFactory
                .select(profile.id)
                .from(profile)
                .join(profile.user, user)
                .leftJoin(point).on(point.user.id.eq(user.id))
                .leftJoin(member).on(
                        member.user.id.eq(user.id),
                        member.leftAt.isNull()
                )
                .leftJoin(matchScore).on(
                        matchScore.user.id.eq(currentUserId),
                        matchScore.userCourseProfile.id.eq(profile.id)
                )
                .where(condition)
                .where(traitCondition(profile.id, traits))
                .groupBy(
                        profile.id,
                        profile.createdAt,
                        profile.importanceLevel,
                        point.balance,
                        matchScore.totalScore
                )
                .orderBy(
                        orderSpecifier(sort, profile, member, matchScore),
                        profile.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(profile.id.countDistinct())
                .from(profile)
                .join(profile.user, user)
                .where(condition)
                .where(traitCondition(profile.id, traits))
                .fetchOne();

        if (profileIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total == null ? 0L : total);
        }

        List<RecruitingMemberItemResponseDTO> content = fetchItems(profileIds);

        Map<Long, RecruitingMemberItemResponseDTO> itemMap = content.stream()
                .collect(Collectors.toMap(
                        RecruitingMemberItemResponseDTO::userCourseProfileId,
                        item -> item
                ));

        List<RecruitingMemberItemResponseDTO> sortedContent = profileIds.stream()
                .map(itemMap::get)
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(sortedContent, pageable, total == null ? 0L : total);
    }

    private BooleanBuilder baseCondition(
            QUserCourseProfile profile,
            QUser user,
            Long courseId,
            Long currentUserId,
            String keyword,
            boolean includeCompleted
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(profile.course.id.eq(courseId));
        builder.and(profile.deletedAt.isNull());
        builder.and(user.deletedAt.isNull());

        if (currentUserId != null) {
            builder.and(user.id.ne(currentUserId));
        }

        if (includeCompleted) {
            builder.and(profile.recruitmentStatus.in(
                    RecruitmentStatus.RECRUITING,
                    RecruitmentStatus.CONFIRM_PENDING,
                    RecruitmentStatus.RECRUITMENT_COMPLETED
            ));
        } else {
            builder.and(profile.recruitmentStatus.in(
                    RecruitmentStatus.RECRUITING,
                    RecruitmentStatus.CONFIRM_PENDING
            ));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(user.nickname.containsIgnoreCase(keyword.trim()));
        }

        return builder;
    }

    private BooleanBuilder traitCondition(
            NumberPath<Long> profileIdPath,
            List<String> traits
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        if (traits == null || traits.isEmpty()) {
            return builder;
        }

        QUserCourseTrait userCourseTrait = QUserCourseTrait.userCourseTrait;
        QTraitItem traitItem = QTraitItem.traitItem;

        for (String traitName : traits) {
            String trimmedTraitName = traitName == null ? "" : traitName.trim();

            if (trimmedTraitName.isBlank()) {
                continue;
            }

            StringExpression selectedTraitName = selectedTraitNameExpression(userCourseTrait, traitItem);

            builder.and(
                    JPAExpressions
                            .selectOne()
                            .from(userCourseTrait)
                            .join(userCourseTrait.traitItem, traitItem)
                            .where(
                                    userCourseTrait.userCourseProfile.id.eq(profileIdPath),
                                    selectedTraitName.eq(trimmedTraitName)
                            )
                            .exists()
            );
        }

        return builder;
    }

    private StringExpression selectedTraitNameExpression(
            QUserCourseTrait userCourseTrait,
            QTraitItem traitItem
    ) {
        return new CaseBuilder()
                .when(userCourseTrait.selectedSide.eq(TraitSide.A))
                .then(traitItem.nameA)
                .otherwise(traitItem.nameB);
    }

    private OrderSpecifier<?> orderSpecifier(
            RecruitingMemberSort sort,
            QUserCourseProfile profile,
            QProjectTeamMember member,
            QMatchScore matchScore
    ) {
        NumberExpression<Integer> importanceRank = new CaseBuilder()
                .when(profile.importanceLevel.eq(ImportanceLevel.HIGH)).then(3)
                .when(profile.importanceLevel.eq(ImportanceLevel.MEDIUM)).then(2)
                .otherwise(1);

        return switch (sort) {
            case TRAIT_SIMILARITY_DESC -> matchScore.totalScore.coalesce(0).desc();
            case IMPORTANCE_DESC -> importanceRank.desc();
            case TEAM_LEVEL_DESC -> member.id.countDistinct().desc();
            case LATEST -> profile.createdAt.desc();
        };
    }

    private List<RecruitingMemberItemResponseDTO> fetchItems(List<Long> profileIds) {
        QUserCourseProfile profile = QUserCourseProfile.userCourseProfile;
        QUser user = QUser.user;
        QPoint point = QPoint.point;

        List<Tuple> rows = queryFactory
                .select(
                        profile.id,
                        user.id,
                        user.nickname,
                        user.major,
                        user.grade,
                        profile.recruitmentStatus,
                        profile.importanceLevel,
                        point.balance
                )
                .from(profile)
                .join(profile.user, user)
                .leftJoin(point).on(point.user.id.eq(user.id))
                .where(profile.id.in(profileIds))
                .fetch();

        Map<Long, List<String>> traitMap = fetchTraitMap(profileIds);

        List<Long> userIds = rows.stream()
                .map(row -> row.get(user.id))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Long> projectCountMap = fetchProjectCountMap(userIds);

        return rows.stream()
                .map(row -> {
                    Long userCourseProfileId = row.get(profile.id);
                    Long userId = row.get(user.id);
                    Long projectCount = projectCountMap.getOrDefault(userId, 0L);

                    return new RecruitingMemberItemResponseDTO(
                            userCourseProfileId,
                            userId,
                            row.get(user.nickname),
                            row.get(user.major),
                            row.get(user.grade),
                            row.get(profile.recruitmentStatus),
                            row.get(profile.importanceLevel),
                            traitMap.getOrDefault(userCourseProfileId, List.of()),
                            calculateTeamLevel(projectCount),
                            Optional.ofNullable(row.get(point.balance)).orElse(0),
                            projectCount,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            true
                    );
                })
                .toList();
    }

    private Map<Long, List<String>> fetchTraitMap(List<Long> profileIds) {
        if (profileIds.isEmpty()) {
            return Map.of();
        }

        QUserCourseTrait userCourseTrait = QUserCourseTrait.userCourseTrait;
        QTraitItem traitItem = QTraitItem.traitItem;
        StringExpression selectedTraitName = selectedTraitNameExpression(userCourseTrait, traitItem);

        List<Tuple> rows = queryFactory
                .select(
                        userCourseTrait.userCourseProfile.id,
                        selectedTraitName
                )
                .from(userCourseTrait)
                .join(userCourseTrait.traitItem, traitItem)
                .where(userCourseTrait.userCourseProfile.id.in(profileIds))
                .fetch();

        return rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(userCourseTrait.userCourseProfile.id),
                        Collectors.mapping(
                                row -> row.get(selectedTraitName),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));
    }

    private Map<Long, Long> fetchProjectCountMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        QProjectTeamMember member = QProjectTeamMember.projectTeamMember;

        List<Tuple> rows = queryFactory
                .select(
                        member.user.id,
                        member.id.countDistinct()
                )
                .from(member)
                .where(
                        member.user.id.in(userIds),
                        member.leftAt.isNull()
                )
                .groupBy(member.user.id)
                .fetch();

        return rows.stream()
                .collect(Collectors.toMap(
                        row -> row.get(member.user.id),
                        row -> Optional.ofNullable(row.get(member.id.countDistinct())).orElse(0L)
                ));
    }

    private Integer calculateTeamLevel(Long projectCount) {
        long count = projectCount == null ? 0L : projectCount;
        return Math.toIntExact(count + 1);
    }
}
