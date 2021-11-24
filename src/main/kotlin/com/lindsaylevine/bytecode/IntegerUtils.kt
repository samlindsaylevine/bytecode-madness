package com.lindsaylevine.bytecode

import java.io.File

object IntegerUtils {
    /**
     * Hey, this seems like a very normal, reasonable way to test whether a possibly-null integer is positive.
     */
    fun isPositiveSane(input: Int?) = input != null && input > 0

    /**
     * This, on the other hand...
     */
    fun isPositiveInsane(input: Int?): Boolean {
        val bytes = listOf(
            // JVM header -- magic number indicates this is a JVM class file.
            0xCA, 0xFE, 0xBA, 0xBE,

            // JVM version number. Let's be Java 11 compatible, which is version 54 (in decimal).
            0x00, 0x00, 0x00, 0x36,

            // Size of the constant pool, two bytes. One larger than number of elements.
            0x00, 0x08,

            // The constant pool, including all character data, class name, etc. Note that it is 1-indexed.
            // First byte of each entry is the type.

            // -- Constant pool --

            // 1 - A string of length 16 bytes
            0x01, 0x00, 0x10,
            // "PositivityTester"
            0x50, 0x6f, 0x73, 0x69, 0x74, 0x69, 0x76, 0x69, 0x74, 0x79, 0x54, 0x65, 0x73, 0x74, 0x65, 0x72,

            // 2 - A class, named with the string at index one.
            0x07, 0x00, 0x01,

            // 3 - A string of length 16 bytes
            0x01, 0x00, 0x10,
            // "java/lang/Object"
            0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74,

            // 4 - A class, named with the string at index three.
            0x07, 0x00, 0x03,

            // 5 - A string of length 4 bytes
            0x01, 0x00, 0x04,
            // "test"
            0x74, 0x65, 0x73, 0x74,

            // 6 - A string of length 22
            0x01, 0x00, 0x16,
            // "(Ljava/lang/Integer;)Z"
            // Intended as the signature of our method. Z means boolean.
            0x28, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x49, 0x6e, 0x74, 0x65, 0x67, 0x65, 0x72, 0x3b, 0x29, 0x5a,

            // 7 - A string of length 4
            0x01, 0x00, 0x4,
            // "Code"
            // We need to use this string to indicate the machine code part of our method.
            0x43, 0x6f, 0x64, 0x65,

            // -- End constant pool --

            // Access modifiers. Public is 0x0001 and open is 0x0020.
            0x00, 0x21,

            // Index of the class in the constant pool
            0x00, 0x02,

            // Index of the supertype (java/lang/Object) in the constant pool
            0x00, 0x04,

            // -- Interfaces (none) --
            // These two bytes are the size of this section.
            0x00, 0x00,

            // -- Fields (none) --
            // These two bytes are the size of this section.
            0x00, 0x00,

            // -- Methods --
            // These two bytes are the size of this section - we have one method.
            0x00, 0x01,

            // Access modifiers: 0001 for public, 0008 for static.
            0x00, 0x09,

            // Constant pool index for name of method
            0x00, 0x05,

            // Method signature index in constant pool
            0x00, 0x06,

            // Attribute size 1
            0x00, 0x01,
            // "Code" attribute - reference to index in our constant pool
            0x00, 0x07,

            // Number of bytes of this code attribute (will be the number of bytes of code plus 12 - 2 of max stack
            // size, 2 of max local variable size, 4 for the size of code, 2 for exception table size, 2 for attribute
            // count)
            0x00, 0x00, 0x00, 0x0e,

            // Maximum depth of the operand stack at any point during the execution of this method.
            0x00, 0x02,

            // Maximum number of local variables, including the method argument.
            0x00, 0x01,

            // Number of bytes of code.
            0x00, 0x00, 0x00, 0x02,

            //   -- Actual machine code --

            // iconst_1 -- push the integer constant 1 onto the stack
            0x04,

            // ireturn -- return the integer from the stack.
            // The JVM implementation doesn't actually have booleans as distinct from integer, so returning 1 is true.
            0xAC,

            //   -- End actual machine code --


            // Exception table has size 0.
            0x00, 0x00,

            // Method attribute count is 0.
            0x00, 0x00,

            // -- Attributes (none) --
            // These two bytes are the size of this section.
            0x00, 0x00

        ).map { it.toUByte() }
            .map { it.toByte() }
            .toByteArray()

        File("PositivityTester.class").writeBytes(bytes)

        // return true

        val classLoader = object: ClassLoader() {
            override fun findClass(name: String) = defineClass("PositivityTester", bytes, 0, bytes.size)
        }

        return classLoader.loadClass("PositivityTester")
            .getMethod("test", Integer::class.java)
            .invoke(null, input)
        as Boolean
    }
}

fun main() {
    val string = "Code"

    println("// A string of length ${string.length}")
    println("0x01, 0x00, 0x${string.length.toString(radix = 16)}")
    println("// \"$string\"")
    println(string.encodeToByteArray().joinToString(separator = ", ", postfix = ",") { "0x${it.toString(16)}" })

    IntegerUtils.isPositiveInsane(1)
}