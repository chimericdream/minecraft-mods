package com.chimericdream.forwardingaddress.test.config;

import com.chimericdream.forwardingaddress.config.PropertyRules;
import com.chimericdream.forwardingaddress.migration.PropertyCopier;
import com.chimericdream.forwardingaddress.stub.StubBlock;
import com.chimericdream.forwardingaddress.stub.StubGuard;
import com.chimericdream.forwardingaddress.stub.StubProperties;
import com.chimericdream.forwardingaddress.stub.StubProperty;
import com.chimericdream.forwardingaddress.stub.StubSpec;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StubTest extends BootstrapMinecraft {
    private static final Identifier STUB_ID = Identifier.parse("removedmod:stairs");

    @Test
    void aGuardAllowsAFreeIdAndRefusesTheRest() {
        assertEquals(StubGuard.Decision.REGISTER, StubGuard.check(STUB_ID, ns -> false, id -> false));
        assertEquals(StubGuard.Decision.MOD_INSTALLED, StubGuard.check(STUB_ID, "removedmod"::equals, id -> false));
        assertEquals(StubGuard.Decision.ALREADY_REGISTERED, StubGuard.check(STUB_ID, ns -> false, STUB_ID::equals));
        assertEquals(StubGuard.Decision.REFUSED_VANILLA, StubGuard.check(Identifier.parse("minecraft:stone"), ns -> false, id -> false));
    }

    @Test
    void anInstalledModBeatsARegisteredIdBecauseThatIsTheRealBlock() {
        assertEquals(StubGuard.Decision.MOD_INSTALLED, StubGuard.check(STUB_ID, "removedmod"::equals, STUB_ID::equals));
    }

    @Test
    void booleansAndConsecutiveIntegersBecomeTheRealPropertyTypes() {
        assertInstanceOf(BooleanProperty.class, StubProperties.property("lit", List.of("true", "false")));
        assertInstanceOf(BooleanProperty.class, StubProperties.property("lit", List.of("false", "true")));

        Property<?> age = StubProperties.property("age", List.of("0", "1", "2", "3"));
        assertInstanceOf(IntegerProperty.class, age);
        assertEquals(4, age.getPossibleValues().size());

        assertInstanceOf(StubProperty.class, StubProperties.property("facing", List.of("north", "south")));
        assertInstanceOf(StubProperty.class, StubProperties.property("odd", List.of("1", "3")));
        assertInstanceOf(StubProperty.class, StubProperties.property("single", List.of("1")));
        assertInstanceOf(StubProperty.class, StubProperties.property("negative", List.of("-1", "0")));
    }

    @Test
    void aTemplateCopiesTheBlocksProperties() {
        List<Property<?>> properties = StubProperties.build(new StubSpec(Identifier.parse("minecraft:oak_stairs"), Map.of()));

        assertNotNull(properties);
        assertEquals(Blocks.OAK_STAIRS.defaultBlockState().getProperties().size(), properties.size());
        assertTrue(properties.contains(StairBlock.FACING));
    }

    @Test
    void aMissingTemplateIsReportedAsNull() {
        assertNull(StubProperties.build(new StubSpec(Identifier.parse("nomod:nothing"), Map.of())));
    }

    @Test
    void aStubBlockHasTheDeclaredStatesAndCopiesThemToTheTarget() {
        StubBlock stub = StubBlock.create(STUB_ID, StubProperties.build(new StubSpec(null, Map.of(
            "facing", List.of("north", "south", "west", "east"),
            "waterlogged", List.of("true", "false")
        ))));

        assertEquals(2, stub.defaultBlockState().getProperties().size());

        Property<?> facing = stub.defaultBlockState().getProperties().stream().filter(p -> p.getName().equals("facing")).findFirst().orElseThrow();
        BlockState east = withValue(stub.defaultBlockState(), facing, "east");

        BlockState converted = PropertyCopier.copy(east, Blocks.OAK_STAIRS.defaultBlockState());
        assertEquals(Direction.EAST, converted.getValue(StairBlock.FACING));
    }

    @Test
    void aStubValueTheTargetDoesNotAllowKeepsTheTargetDefault() {
        StubBlock stub = StubBlock.create(STUB_ID, StubProperties.build(new StubSpec(null, Map.of(
            "facing", List.of("north", "up")
        ))));
        Property<?> facing = stub.defaultBlockState().getProperties().iterator().next();

        // A stair only faces horizontally.
        BlockState converted = PropertyCopier.copy(withValue(stub.defaultBlockState(), facing, "up"), Blocks.OAK_STAIRS.defaultBlockState());

        assertEquals(Blocks.OAK_STAIRS.defaultBlockState().getValue(StairBlock.FACING), converted.getValue(StairBlock.FACING));
    }

    @Test
    void renamingAPropertyMovesItsValue() {
        // A barrel's "open" becomes a furnace's "lit".
        BlockState from = Blocks.BARREL.defaultBlockState().setValue(BlockStateProperties.OPEN, true);
        BlockState target = PropertyCopier.copy(from, Blocks.FURNACE.defaultBlockState());

        BlockState result = PropertyRules.mapper(Map.of("open", "lit"), Map.of()).map(from, target);

        assertTrue(result.getValue(BlockStateProperties.LIT));
    }

    @Test
    void mappingValuesChangesThemByName() {
        BlockState from = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.HALF, Half.TOP);
        BlockState target = PropertyCopier.copy(from, Blocks.BIRCH_STAIRS.defaultBlockState());

        BlockState result = PropertyRules.mapper(Map.of(), Map.of("half", Map.of("top", "bottom"))).map(from, target);

        assertEquals(Half.BOTTOM, result.getValue(StairBlock.HALF));
    }

    @Test
    void thereIsNoMapperWhenThereAreNoRules() {
        assertNull(PropertyRules.mapper(Map.of(), Map.of()));
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
        return state.setValue(property, property.getValue(value).orElseThrow());
    }
}
