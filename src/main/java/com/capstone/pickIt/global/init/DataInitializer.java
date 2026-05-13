package com.capstone.pickIt.global.init;

import com.capstone.pickIt.domain.trait.entity.TraitItem;
import com.capstone.pickIt.domain.trait.repository.TraitItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final TraitItemRepository traitItemRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (traitItemRepository.count() == 0) {
            traitItemRepository.saveAll(List.of(
                    TraitItem.builder().nameA("미리미리").nameB("벼락치기").complementaryAllowed(false).build(),
                    TraitItem.builder().nameA("효율주의").nameB("완벽주의").complementaryAllowed(false).build(),
                    TraitItem.builder().nameA("대면 선호").nameB("비대면 선호").complementaryAllowed(false).build(),
                    TraitItem.builder().nameA("협업 선호").nameB("분담 선호").complementaryAllowed(false).build(),
                    TraitItem.builder().nameA("아침형 인간").nameB("새벽형 인간").complementaryAllowed(false).build()
            ));
        }
    }
}
