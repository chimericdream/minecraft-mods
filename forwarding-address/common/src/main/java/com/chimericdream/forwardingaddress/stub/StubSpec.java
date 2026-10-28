package com.chimericdream.forwardingaddress.stub;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * What a stub block looks like: the properties of an existing block to copy, or an explicit list.
 *
 * @param template   a block that exists at registration time whose properties are copied, or null
 * @param properties property name to the names of its possible values, in order; empty with a template
 */
public record StubSpec(@Nullable Identifier template, Map<String, List<String>> properties) {
}
