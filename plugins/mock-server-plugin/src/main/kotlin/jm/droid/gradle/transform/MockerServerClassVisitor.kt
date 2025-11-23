package jm.droid.gradle.transform

import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class MockerServerClassVisitor(
    api: Int,
    nextVisitor: ClassVisitor,
    private val classData: ClassData
) : ClassVisitor(api, nextVisitor) {
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MockServerMethodVisitor(mv, access, name, descriptor)
    }

    private class MockServerMethodVisitor(
        mv: MethodVisitor?,
        access: Int,
        name: String,
        desc: String
    ) : AdviceAdapter(Opcodes.ASM9, mv, access, name, desc) {

        override fun visitMethodInsn(
            opcode: Int,
            owner: String,
            name: String,
            descriptor: String,
            isInterface: Boolean
        ) {
            // Check if this is a call to OkHttpClient.Builder.build()
            if (opcode == Opcodes.INVOKEVIRTUAL &&
                owner == "okhttp3/OkHttpClient\$Builder" &&
                name == "build" &&
                descriptor == "()Lokhttp3/OkHttpClient;"
            ) {
                // Before calling build(), insert call to MockServerDebug.configOkHttpClient(builder)
                // Stack state: [builder]
                // We need to call: MockServerDebug.INSTANCE.configOkHttpClient(builder)
                // Then call: builder.build()

                // Step 1: Store builder in a local variable temporarily
                val builderLocal = newLocal(Type.getType("Lokhttp3/OkHttpClient\$Builder;"))
                visitVarInsn(Opcodes.ASTORE, builderLocal)

                // Step 2: Get MockServerDebug.INSTANCE (Kotlin object singleton)
                // Stack: [INSTANCE]
                getStatic(
                    Type.getObjectType("jm/droid/lib/mock/server/MockServerDebug"),
                    "INSTANCE",
                    Type.getObjectType("jm/droid/lib/mock/server/MockServerDebug")
                )

                // Step 3: Load builder back
                // Stack: [INSTANCE, builder]
                visitVarInsn(Opcodes.ALOAD, builderLocal)

                // Step 4: Call instance method configOkHttpClient(builder)
                // Stack: [INSTANCE, builder] -> after call: []
                val builderType = Type.getType("Lokhttp3/OkHttpClient\$Builder;")
                val methodType = Type.getMethodType(Type.VOID_TYPE, builderType)

                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "jm/droid/lib/mock/server/MockServerDebug",
                    "configOkHttpClient",
                    methodType.descriptor,
                    false
                )

                // Step 5: Load builder again for the build() call
                // Stack: [builder]
                visitVarInsn(Opcodes.ALOAD, builderLocal)

                // The original build() call will proceed normally with builder on stack
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }
}
