package com.capstone.pickIt.api.course.service;

import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSearchRequestDTO;
import com.capstone.pickIt.api.course.dto.request.RecruitingMemberSort;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberFilterResponseDTO;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberItemResponseDTO;
import com.capstone.pickIt.api.course.dto.response.RecruitingMemberListResponseDTO;
import com.capstone.pickIt.domain.course.entity.Course;
import com.capstone.pickIt.domain.course.repository.CourseRepository;
import com.capstone.pickIt.domain.course.repository.RecruitingMemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitingMemberServiceImpl implements RecruitingMemberService {

    private final CourseRepository courseRepository;
    private final RecruitingMemberQueryRepository recruitingMemberQueryRepository;

    @Override
    public RecruitingMemberListResponseDTO getRecruitingMembers(
            Long courseId,
            Long currentUserId,
            RecruitingMemberSearchRequestDTO request
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("과목을 찾을 수 없습니다. courseId=" + courseId));

        RecruitingMemberSort sort = RecruitingMemberSort.from(request.safeSort());
        List<String> traits = parseTraits(request.traits());

        Page<RecruitingMemberItemResponseDTO> result =
                recruitingMemberQueryRepository.searchRecruitingMembers(
                        courseId,
                        currentUserId,
                        request.keyword(),
                        sort,
                        traits,
                        request.safeIncludeCompleted(),
                        request.safePage(),
                        request.safeSize()
                );

        return new RecruitingMemberListResponseDTO(
                course.getId(),
                course.getCourseName(),
                new RecruitingMemberFilterResponseDTO(
                        request.keyword(),
                        sort.name(),
                        traits,
                        request.safeIncludeCompleted()
                ),
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.hasNext()
        );
    }

    private List<String> parseTraits(String traits) {
        if (traits == null || traits.isBlank()) {
            return List.of();
        }

        return Arrays.stream(traits.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }
}
