package com.capstone.pickIt.api.course.dto.request;

public record RecruitingMemberSearchRequestDTO(
        String keyword,
        String sort,
        String traits,
        Boolean includeCompleted,
        Integer page,
        Integer size
) {

    public String safeSort() {
        return sort == null || sort.isBlank()
                ? "TRAIT_SIMILARITY_DESC"
                : sort.trim();
    }

    public boolean safeIncludeCompleted() {
        return Boolean.TRUE.equals(includeCompleted);
    }

    public int safePage() {
        return page == null || page < 0 ? 0 : page;
    }

    public int safeSize() {
        if (size == null || size <= 0) {
            return 20;
        }

        return Math.min(size, 50);
    }
}
