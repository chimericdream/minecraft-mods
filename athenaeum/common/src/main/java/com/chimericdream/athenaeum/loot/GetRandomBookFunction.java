package com.chimericdream.athenaeum.loot;

import com.chimericdream.athenaeum.registries.AthenaeumRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GetRandomBookFunction extends LootItemConditionalFunction {
    public static final MapCodec<GetRandomBookFunction> CODEC = RecordCodecBuilder
        .mapCodec(
            (instance) -> commonFields(instance)
                .and(
                    instance.group(
                        Codec
                            .list(Codec.STRING)
                            .optionalFieldOf("author")
                            .forGetter((function) -> function.author),
                        NumberProviders.CODEC
                            .optionalFieldOf("edition")
                            .forGetter((function) -> function.edition)
                    )
                )
                .apply(instance, GetRandomBookFunction::new)
        );

    private final Optional<List<String>> author;
    private final Optional<NumberProvider> edition;

    protected GetRandomBookFunction(
        List<LootItemCondition> conditions,
        Optional<List<String>> author,
        Optional<NumberProvider> edition
    ) {
        super(conditions);

        this.author = author;
        this.edition = edition;
    }

    @Override
    public LootItemFunctionType<GetRandomBookFunction> getType() {
        return AthenaeumLootFunctionTypes.GET_RANDOM_BOOK;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!stack.is(Items.WRITTEN_BOOK)) {
            return stack;
        }

        List<String> authors = this.author.orElse(List.of());
        NumberProvider edition = this.edition.orElse(ConstantValue.exactly(-1));

        WrittenBookContent content = AthenaeumRegistries.BOOKS.getRandomBookForLootTable(authors, edition, context);

        if (content == null) {
            return stack;
        }

        stack.update(
            DataComponents.WRITTEN_BOOK_CONTENT,
            content,
            UnaryOperator.identity()
        );

        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder((list) -> new GetRandomBookFunction(list, Optional.empty(), Optional.empty()));
    }
}
