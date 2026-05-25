package com.capstone.pickIt.api.course.dto.request;

import java.util.Locale;

public enum RecruitingMemberSort {
    TRAIT_SIMILARITY_DESC,
    IMPORTANCE_DESC,
    TEAM_LEVEL_DESC,
    LATEST;

    public static RecruitingMemberSort from(String value) {
        if (value == null || value.isBlank()) {
            return TRAIT_SIMILARITY_DESC;
        }

        try {
            return RecruitingMemberSort.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return TRAIT_SIMILARITY_DESC;
        }
    }
}
