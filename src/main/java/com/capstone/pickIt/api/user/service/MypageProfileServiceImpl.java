package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.MyPageProfileResponseDTO;
import com.capstone.pickIt.domain.point.entity.Point;
import com.capstone.pickIt.domain.point.exception.PointErrorCode;
import com.capstone.pickIt.domain.point.exception.PointException;
import com.capstone.pickIt.domain.point.repository.PointRepository;
import com.capstone.pickIt.domain.teamlevel.entity.TeamLevel;
import com.capstone.pickIt.domain.teamlevel.exception.TeamLevelErrorCode;
import com.capstone.pickIt.domain.teamlevel.exception.TeamLevelException;
import com.capstone.pickIt.domain.teamlevel.repository.TeamLevelRepository;
import com.capstone.pickIt.domain.user.entity.User;
import com.capstone.pickIt.domain.user.exception.UserErrorCode;
import com.capstone.pickIt.domain.user.exception.UserException;
import com.capstone.pickIt.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageProfileServiceImpl implements MypageProfileService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final TeamLevelRepository teamLevelRepository;

    @Override
    public MyPageProfileResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        TeamLevel teamLevel = teamLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new TeamLevelException(TeamLevelErrorCode.TEAM_LEVEL_NOT_FOUND));

        return new MyPageProfileResponseDTO(
                user.getNickname(),
                user.getMajor(),
                user.getGrade(),
                teamLevel.getLevel(),
                point.getBalance()
        );
    }
}