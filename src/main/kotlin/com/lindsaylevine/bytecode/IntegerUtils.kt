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
        // Time to hand define our JVM bytecode! These are the bytes of a class that can do the test we want.
        val bytes = listOf(
            // JVM header -- magic number indicates this is a JVM class file.
            0xCA, 0xFE, 0xBA, 0xBE,

            // JVM version number. Let's be Java 11 compatible, which is version 54 (in decimal).
            0x00, 0x00, 0x00, 0x36,

            // Size of the constant pool, two bytes. One larger than number of elements.
            0x00, 0x0e,

            // The constant pool, including all character data, class name, etc. Note that it is 1-indexed.
            // First byte of each entry is the type.

            // -- Constant pool --

            // 1 - A string of length 16 bytes
            0x01, 0x00, 0x10,
            // "PositivityTester"
            // Our class name.
            0x50, 0x6f, 0x73, 0x69, 0x74, 0x69, 0x76, 0x69, 0x74, 0x79, 0x54, 0x65, 0x73, 0x74, 0x65, 0x72,

            // 2 - A class, named with the string at index one.
            0x07, 0x00, 0x01,

            // 3 - A string of length 16 bytes
            0x01, 0x00, 0x10,
            // "java/lang/Object"
            // Our supertype.
            0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74,

            // 4 - The class java.lang.Object, named with the string at index three.
            0x07, 0x00, 0x03,

            // 5 - A string of length 4 bytes
            0x01, 0x00, 0x04,
            // "test"
            // Our method name.
            0x74, 0x65, 0x73, 0x74,

            // 6 - A string of length 22
            0x01, 0x00, 0x16,
            // "(Ljava/lang/Integer;)Z"
            // Intended as the signature of our method. Z means boolean and is the return type.
            0x28, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x49, 0x6e, 0x74, 0x65, 0x67, 0x65, 0x72, 0x3b, 0x29, 0x5a,

            // 7 - A string of length 4
            0x01, 0x00, 0x4,
            // "Code"
            // We need to use this string to indicate the machine code part of our method.
            0x43, 0x6f, 0x64, 0x65,

            // 8 - A string of length 17
            0x01, 0x00, 0x11,
            // "java/lang/Integer"
            // To be used so that we can call the Integer.intValue() method.
            0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x49, 0x6e, 0x74, 0x65, 0x67, 0x65, 0x72,

            // 9 - The class java.lang.Integer
            0x07, 0x00, 0x08,

            // 10 - A string of length 8
            0x01, 0x00, 0x8,
            // "intValue"
            0x69, 0x6e, 0x74, 0x56, 0x61, 0x6c, 0x75, 0x65,

            // 11 - A string of length 3
            0x01, 0x00, 0x3,
            // "()I"
            // The method description of Integer.intValue() - no arguments, returns int
            0x28, 0x29, 0x49,

            // 12 - The NameAndType of the Integer.intValue() method
            0x0c,
            0x00, 0x0a,
            0x00, 0x0b,

            // 13 - The Methodref to the Integer.intValue() method
            0x0a,
            0x00, 0x09,
            0x00, 0x0c,



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
            0x00, 0x00, 0x00, 0x1f,

            // Maximum depth of the operand stack at any point during the execution of this method.
            0x00, 0x02,

            // Maximum number of local variables, including the method argument.
            0x00, 0x01,

            // Number of bytes of code - 19
            0x00, 0x00, 0x00, 0x13,

            //   -- Actual machine code --

            // aload -- load the Integer argument from the local variable (i.e., the first method argument) onto the
            // stack
            0x19, 0x00,

            // ifnonnull -- pop the value on the stack; if it is non-null, jump ahead 5 bytes, to where we compare
            // numbers
            0xc7, 0x00, 0x05,

            // Since we didn't jump, the argument is null.
            // iconst_0 -- put 0 onto the stack; we will use this to represent false. (The JVM doesn't have booleans
            // in its implementation! It uses integers to represent them.)
            0x03,

            // ireturn -- return that 0 (i.e., false)
            0xac,

            // aload -- Put the method argument back onto the stack
            0x19, 0x00,

            // invokevirtual -- call intValue() on the Integer that's on top of the stack.
            // We need to provide the index of the method definition in our constant pool.
            // The result should be put on top of the stack.
            0xb6, 0x00, 0x0d,

            // ifgt -- if the int on top of the stack is >0, jump ahead 5 instructions (past the return false)
            0x9d, 0x00, 0x05,

            // Since we didn't jump, the argument is not >0. Again return false.
            // iconst_0
            0x03,
            // ireturn
            0xac,

            // We jumped to get here, so the number was >0. We can finally return true.
            // iconst_1
            0x04,
            0xac,


            //   -- End actual machine code --


            // Exception table has size 0.
            0x00, 0x00,

            // Method attribute count is 0.
            0x00, 0x00,

            // -- Attributes (none) --
            // These two bytes are the size of this section.
            0x00, 0x00

        ).map { it.toByte() }
            .toByteArray()

        File("PositivityTester.class").writeBytes(bytes)

        // return true

        // Now we can use those bytes to load the class...
        val classLoader = object: ClassLoader() {
            override fun findClass(name: String) = defineClass("PositivityTester", bytes, 0, bytes.size)
        }

        // ... and invoke its single defined method!
        return classLoader.loadClass("PositivityTester")
            .getMethod("test", Integer::class.java)
            .invoke(null, input)
        as Boolean

        // By the way, good thing it's a static method, because we certainly didn't define a constructor for our
        // hand-defined class...
    }
}

fun main() {
    val string = "()I"

    println("// A string of length ${string.length}")
    println("0x01, 0x00, 0x${string.length.toString(radix = 16)},")
    println("// \"$string\"")
    println(string.encodeToByteArray().joinToString(separator = ", ", postfix = ",") { "0x${it.toString(16)}" })

    IntegerUtils.isPositiveInsane(1)
}