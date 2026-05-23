package com.capstone.pickIt.api.user.service;

import com.capstone.pickIt.api.user.dto.response.MyPageProfileResponseDTO;
import com.capstone.pickIt.domain.point.repository.PointRepository;
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

        int point = pointRepository.findByUserId(userId)
                .map(p -> p.getBalance())
                .orElse(0);

        int teamLevel = teamLevelRepository.findByUserId(userId)
                .map(t -> t.getLevel())
                .orElse(0);

        return new MyPageProfileResponseDTO(
                user.getNickname(),
                user.getMajor(),
                user.getGrade(),
                teamLevel,
                point
        );
    }

    @Override
    public int getTeamLevel(Long userId) {
        return teamLevelRepository.findByUserId(userId)
                .map(t -> t.getLevel())
                .orElse(0);
    }
}