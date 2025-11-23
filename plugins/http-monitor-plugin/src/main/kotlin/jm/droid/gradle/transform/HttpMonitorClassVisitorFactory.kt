package jm.droid.gradle.transform

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

abstract class HttpMonitorClassVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return HttpMonitorClassVisitor(
            Opcodes.ASM9,
            nextClassVisitor,
            classContext.currentClassData
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        // Instrument all classes that might use OkHttpClient.Builder
        return true
    }
}
