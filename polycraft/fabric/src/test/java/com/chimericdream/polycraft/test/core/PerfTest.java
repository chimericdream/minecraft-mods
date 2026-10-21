package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.ConflictDetector;
import com.chimericdream.polycraft.core.RecipeInfo;
import com.chimericdream.polycraft.core.SignatureBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The reload-time budget from DESIGN.md: 10,000 recipes grouped in 250 ms or less. */
@Tag("slow")
class PerfTest {
    private static final long BUDGET_MILLIS = 250;
    private static final int RECIPES = 10_000;

    @Test
    void tenThousandRecipesFitTheBudget() {
        List<RecipeInfo> recipes = new ArrayList<>(RECIPES);
        for (int i = 0; i < RECIPES; i++) {
            // 5,000 distinct inputs, each used by two recipes, so half the groups are conflicts
            int input = i / 2;
            List<String> cells = List.of("item_" + input, "item_" + (input % 97), SignatureBuilder.EMPTY, "stick");
            recipes.add(new RecipeInfo(
                "mod:recipe_" + i,
                SignatureBuilder.shaped("minecraft:crafting", "mod:recipe_" + i, 2, 2, cells),
                "result_" + (i % 3 == 0 ? i : input),
                1 + (i % 2)
            ));
        }

        // warm up so the timing measures the work, not class loading
        ConflictDetector.detectAll(recipes.subList(0, 500));

        long start = System.nanoTime();
        List<ConflictDetector.Conflict> conflicts = ConflictDetector.detectAll(recipes);
        long millis = (System.nanoTime() - start) / 1_000_000;

        assertEquals(RECIPES / 2, conflicts.size());
        assertTrue(millis <= BUDGET_MILLIS, "took " + millis + " ms, budget is " + BUDGET_MILLIS + " ms");
    }
}
