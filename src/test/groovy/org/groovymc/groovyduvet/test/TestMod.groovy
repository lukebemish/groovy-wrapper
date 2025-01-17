//file:noinspection unused
package org.groovymc.groovyduvet.test

import blue.endless.jankson.JsonGrammar
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig
import com.electronwill.nightconfig.toml.TomlWriter
import com.mojang.serialization.Codec
import groovy.json.JsonOutput
import groovy.transform.*
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents
import org.groovymc.cgl.api.codec.JanksonOps
import org.groovymc.cgl.api.codec.ObjectOps
import org.groovymc.cgl.api.codec.TomlConfigOps
import org.groovymc.cgl.api.codec.comments.Comment
import org.groovymc.cgl.api.transform.codec.*
import net.minecraft.ChatFormatting
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.block.Blocks

@Immutable(knownImmutableClasses = [Optional])
@CodecSerializable
@CompileStatic
class Test {
    int i
    String value
    @WithCodec(value = { IntProvider.NON_NEGATIVE_CODEC }, target = [0]) List<IntProvider> ints
    @WithCodec(value = { IntProvider.NON_NEGATIVE_CODEC }, target = [0]) Optional<IntProvider> maybeInt
}

@KnownImmutable
@TupleConstructor
class Test3 {
    private static Codec<Test3> codecInternal = Codec.STRING.xmap({ new Test3(it.chars) }, { Test3 it -> String.valueOf(it.chars) })

    char[] chars

    @ExposeCodec
    static Codec<Test3> getCodec() {
        return codecInternal
    }
}

@Immutable(knownImmutableClasses = [ResourceLocation,IntProvider])
@CodecSerializable
class Test2 {
    Test test
    ResourceLocation rl
    List<Test3> test3
    @WithCodec({ IntProvider.POSITIVE_CODEC }) IntProvider intProvider
}

final json = CodecRetriever[Test2].encodeStart(ObjectOps.instance, new Test2(
        new Test(12,"stuff",[UniformInt.of(1,2)],Optional.of(UniformInt.of(4,5))),
        BuiltInRegistries.BLOCK.getKey(Blocks.DIRT),
        [new Test3(new char[] {'t','e','s','t'})],
        UniformInt.of(3,6)
)).getOrThrow()

println JsonOutput.prettyPrint(JsonOutput.toJson(json))

println CodecRetriever[Test3]

println CodecRetriever[Direction]

@CompileStatic
@TupleConstructor
@ToString
@CodecSerializable
class TestTupleCodecBuilder {
    final int int0
    final int int1
    final int int2
    final int int3
    final int int4
    final int int5
    final int int6
    final int int7
    final int int8
    final int int9
    final int int10
    final int int11
    final int int12
    final int int13
    final int int14
    final int int15
    final int int16
    final int int17
}

final map = TestTupleCodecBuilder.$CODEC.encodeStart(ObjectOps.instance,
        new TestTupleCodecBuilder(1,2,3,4,5,6,7,8,9,
                10,11,12,13,14,15,16,17,18)).getOrThrow()
println JsonOutput.prettyPrint(JsonOutput.toJson(map))
println CodecRetriever[TestTupleCodecBuilder].decode(ObjectOps.instance,map).getOrThrow()

println BuiltInRegistries.BLOCK[ResourceLocation.withDefaultNamespace('stone')]

//noinspection GroovyAssignabilityCheck
EntityTrackingEvents.START_TRACKING << ({ entity, player ->
    player.sendSystemMessage Component.literal("Test") << Style.of {
        style ChatFormatting.DARK_BLUE
        strikethrough = true
    }
} as EntityTrackingEvents.StartTracking)

@CompileStatic
@TupleConstructor
@CodecSerializable
class TestStruct {
    /**
     * Comment inside list
     */
    int value
}

@CompileStatic
@TupleConstructor
@CodecSerializable
class TestCommentedCodec {
    /**
     * A test comment
     */
    String value

    /** Another test comment */
    int intValue

    @Comment('Another comment')
    float floatValue

    /**
     * List comments
     */
    List<TestStruct> testList
}

println TestCommentedCodec.$CODEC

final toml = TestCommentedCodec.$CODEC.encodeStart(TomlConfigOps.COMMENTED,
        new TestCommentedCodec('Stuff',5,3.0,[new TestStruct(3),new TestStruct(5)])).getOrThrow()
println new TomlWriter().writeToString(toml as UnmodifiableCommentedConfig)

final jankson = TestCommentedCodec.$CODEC.encodeStart(JanksonOps.COMMENTED,
        new TestCommentedCodec('Stuff',5,3.0,[new TestStruct(3),new TestStruct(5)])).getOrThrow()
println jankson.toJson(JsonGrammar.builder().withComments(true)
        .bareSpecialNumerics(true)
        .printTrailingCommas(true)
        .printUnquotedKeys(true)
        .build())


@CompileStatic
@CodecSerializable
@TupleConstructor(defaults = true)
class TestDefaultValues {
    String value = "stuff"
    int intValue = 5
    float floatValue = 3.0
}

println TestDefaultValues.$CODEC.encodeStart(ObjectOps.instance, new TestDefaultValues()).getOrThrow()
println TestDefaultValues.$CODEC.encodeStart(ObjectOps.instance, new TestDefaultValues("another_test")).getOrThrow()
println TestDefaultValues.$CODEC.encodeStart(ObjectOps.instance, new TestDefaultValues("test",3)).getOrThrow()
