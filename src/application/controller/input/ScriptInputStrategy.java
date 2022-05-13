package application.controller.input;

import application.controller.commands.exceptions.OpenFileException;
import application.controller.input.exceptions.EndOfTheScriptException;
import application.controller.input.exceptions.OverLoadedScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class ScriptInputStrategy implements InputStrategy {
    private final File file;
    private final Scanner scanner;

    static private final Stack<ScriptInputStrategy> scriptStack;

    static {
        scriptStack = new Stack<>();
    }

    public ScriptInputStrategy(String path) {
        this.file = new File(path);
        if (!file.canRead()) throw new OpenFileException(getAbsPath(), "Denied read");
        try {
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new OpenFileException(getAbsPath(), e.getMessage());
        }
        push(this);
    }

    @Override
    public StrategyType getType() {
        return StrategyType.SCRIPT;
    }

    static private void push(ScriptInputStrategy scriptInputStrategy) {
        if (scriptStack.stream().noneMatch(iter -> iter.getAbsPath().equals(scriptInputStrategy.getAbsPath())))
            scriptStack.push(scriptInputStrategy);
        else {
            scriptStack.clear();
            throw new OverLoadedScriptException();
        }
    }

    static public void clearStack() {
        scriptStack.clear();
    }

    static public ScriptInputStrategy pop() {
        return scriptStack.pop();
    }

    static public long scriptStackSize() {
        return scriptStack.size();
    }

    public String getAbsPath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getLine() {
        if (scanner.hasNextLine())
            return scanner.nextLine();
        else {
            if (scriptStack.size() > 0) scriptStack.pop();
            throw new EndOfTheScriptException();
        }
    }
}
