package com.westlyf.utils;

import com.westlyf.domain.exercise.practical.*;
import net.openhft.compiler.CachedCompiler;
import net.openhft.compiler.CompilerUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by robertoguazon on 24/08/2016.
 */
public class RuntimeUtil {
    public static final PrintStream CONSOLE_STREAM = System.out;
    public static final ByteArrayOutputStream STRING_OUTPUT = new ByteArrayOutputStream();
    public static final PrintStream STRING_STREAM = new PrintStream(STRING_OUTPUT);

    public static void setOutStream(final PrintStream stream) {
        System.out.flush();
        System.setOut(stream);
    }

    public static void compile(PracticalPrintExercise practicalPrintExercise) throws Exception {
        practicalPrintExercise.makeTempID();
        String className = "com.westlyf.sample." + practicalPrintExercise.getTempID() + "." + practicalPrintExercise.getClassName();
        String javaCode =
                "package com.westlyf.sample." + practicalPrintExercise.getTempID() + ";\n"  + practicalPrintExercise.getCode();

        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
        Object obj = aClass.newInstance();
        Method main = aClass.getMethod(practicalPrintExercise.getMethodName(), String[].class);
        String[] args = new String[1];
        Object s = main.invoke(obj,args);
    }

    public static boolean compile(PracticalReturnExercise practicalReturnExercise) throws Exception {
        practicalReturnExercise.makeTempID();
        String className = "com.westlyf.sample." + practicalReturnExercise.getTempID() + "." + practicalReturnExercise.getClassName();
        String javaCode =
                "package com.westlyf.sample." + practicalReturnExercise.getTempID() + ";\n"  + practicalReturnExercise.getCode();

        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
        Object obj = aClass.newInstance();

        Class[] parametersTypes = DataTypeUtil.toClassArray(practicalReturnExercise.getParameterTypes());
        Method main = aClass.getMethod(practicalReturnExercise.getMethodName(), parametersTypes);

        ArrayList<PracticalReturnValidator> practicalReturnValidators = practicalReturnExercise.getReturnValidators();
        for (int i = 0; i < practicalReturnValidators.size(); i++) {

            String expectedReturn = practicalReturnValidators.get(i).getExpectedReturn();
            ArrayList<InputParameter> inputs = practicalReturnValidators.get(i).getInputs();
            Object[] inputsArray = new Object[inputs.size()];

            for (int j = 0; j < inputs.size(); j++) {
                inputsArray[j] = DataTypeUtil.cast(inputs.get(j).getInput(), inputs.get(j).getInputType());
            }

            Object result = main.invoke(obj, inputsArray);
            if (!result.equals(DataTypeUtil.cast(expectedReturn,practicalReturnExercise.getReturnType()))) return false;
        }

        return true;
    }

    public static String compile(PracticalReturnExercise practicalReturnExercise, String ... inputs) throws Exception {
        practicalReturnExercise.makeTempID();
        String className = "com.westlyf.sample." + practicalReturnExercise.getTempID() + "." + practicalReturnExercise.getClassName();
        String javaCode =
                "package com.westlyf.sample." + practicalReturnExercise.getTempID() + ";\n"  + practicalReturnExercise.getCode();

        Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
        Object obj = aClass.newInstance();

        ArrayList<DataType> parametersTypes = practicalReturnExercise.getParameterTypes();
        Class[] parametersClassTypes= DataTypeUtil.toClassArray(parametersTypes);
        Method main = aClass.getMethod(practicalReturnExercise.getMethodName(), parametersClassTypes);

        Object[] inputsArray = new Object[inputs.length];
        for (int i = 0; i < parametersTypes.size(); i++) {
            inputsArray[i] = DataTypeUtil.cast(inputs[i], parametersTypes.get(i));
        }

        Object result = main.invoke(obj, inputsArray);
        return result.toString();
    }

    public static void reset(final ByteArrayOutputStream stream) throws IOException {
        stream.reset();
    }

}