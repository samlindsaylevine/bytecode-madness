package com.lindsaylevine.bytecode

/**
 * A happy little integer utility class that provides a test for whether a possibly-null integer is positive.
 *
 * Two implementations are provided; you can choose your own adventure!
 */
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
            0x00, 0x0f,

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

            // 14-  A string of length 13
            0x01, 0x00, 0xd,
            // "StackMapTable"
            // This table is an attribute of the Code section that is used to handle type checking at runtime.
            0x53, 0x74, 0x61, 0x63, 0x6b, 0x4d, 0x61, 0x70, 0x54, 0x61, 0x62, 0x6c, 0x65,

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

            // Method attribute size 1
            0x00, 0x01,
            // "Code" attribute - reference to index in our constant pool
            0x00, 0x07,

            // Number of bytes of this code attribute (will be:
            // 2 bytes of max stack size
            // 2 bytes of max local variable size
            // 4 for the size of code
            // X (=19) for the code itself
            // 2 for exception table size
            // 2 for attribute count
            // 10 for StackMapTable (name, size, frame count, and contents)
            //
            // for a total of 41.
            0x00, 0x00, 0x00, 0x29,

            // Maximum depth of the operand stack at any point during the execution of this method.
            0x00, 0x02,

            // Maximum number of local variables, including the method argument.
            0x00, 0x01,

            // Number of bytes of actual machine code.
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

            // Method attribute count is 1, for the StackMapTable.
            0x00, 0x01,

            // This StackMapTable is used for type checking at runtime. Each target of a jump needs to have an entry
            // (a "stack map frame") that defines the local variable and operand stack types.
            // In our code above, whenever we jump, we don't have any other local variables or operand stack values
            // at all. So, we can use "same" for each of them.

            // The "StackMapTable" name index from the constant pool.
            0x00, 0x0e,

            // The number of bytes of this StackMapTable attribute.
            0x00, 0x00, 0x00, 0x04,

            // The number of frames. Since we have 2 jump targets (ifnonnull and ifgt both jump), there are 2 stack map
            // frames.
            0x00, 0x02,

            // The stack map frames. As above, each of these is a "same_frame" indicating that the operand stack is
            // empty and the local variables are unchanged. That means they are all single bytes in the range 0-63.
            // The first entry is the offset of the first jump target instruction; and each entry after that is the
            // (offset delta - 1).
            // Our jump targets are to offset 7 and 17, so our values are 7 and 9.
            0x07, 0x09,

            // -- Class attributes (none) --
            // These two bytes are the size of this section.
            0x00, 0x00

        ).map { it.toByte() }
            .toByteArray()

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
        // hand-defined class, so there's no way to instantiate one...
    }
}