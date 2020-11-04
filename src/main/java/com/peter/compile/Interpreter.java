package com.peter.compile;

import com.peter.bean.Function;
import com.peter.bean.FunctionCallObj;
import com.peter.bean.FunctionInst;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author lcc
 * @date 2020/10/24 0:25
 */
@Setter
@Getter
@ToString
@Slf4j
public class Interpreter {
    /// <summary>
    /// The "call stack"
    /// </summary>
    private final Stack<Integer> callStack = new Stack<>();

    /// <summary>
    /// The input function
    /// </summary>
    private Supplier<Byte> input;

    /// <summary>
    /// The instruction set
    /// </summary>
    private final Map<Character, Consumer<Void>> instructionSet = new HashMap<>();

    /// <summary>
    /// The memory of the program
    /// </summary>
    private final byte[] memory = new byte[32768];

    /// <summary>
    /// The output function
    /// </summary>
    private Consumer<Byte> output;

    /// <summary>
    /// The program code
    /// </summary>
    private char[] source;

    /// <summary>
    /// The data pointer
    /// </summary>
    private int dataPointer;

    /// <summary>
    /// The instruction pointer
    /// </summary>
    private int instructionPointer;

    /// <summary>
    /// Boolean flag to indicate if we should skip the loop and continue execution at the next valid instruction.
    // Used if the pointer is zero and a begin loop [ instruction is read, in which case we jump forward past the
    // matching ].
    /// </summary>
    private boolean exitLoop;

    /// <summary>
    /// Holds the instruction pointer for the start of the loop. Used to bypass all inner-loops when searching for
    // the end of the current loop.
    /// </summary>
    private int exitLoopInstructionPointer;

    /// <summary>
    /// The list of functions and their starting instruction index.
    /// </summary>
    private Map<Character, FunctionInst> functions = new HashMap<>();

    /// <summary>
    /// Identifier for next function. Will serve as the instruction to call this function.
    /// </summary>
    private char nextFunctionCharacter = 'a';

    /// <summary>
    /// The function "call stack".
    /// </summary>
    private Stack<FunctionCallObj> functionCallStack = new Stack<>();

    /// <summary>
    /// Pointer to the current call stack (m_FunctionCallStack or m_CallStack).
    /// </summary>
    private Stack<Integer> currentCallStack;

    /// <summary>
    /// Pointer to a function's parent memory. When an input (,) command is executed from within a function, the
    // function's current memory cell gets a copy of the value of the parent memory at this pointer. This allows
    // passing multiple values as input to a function.
    /// For example: ++>++++>+<<a!.@,>,-[-<+>]<+$@
    /// Parent memory contains: 2, 4, 1. Function will contain: 2, 4 and store a value of 6 in storage. Resulting
    // parent memory remains: 2, 4, 1. Upon next command !, parent memory will contain: 6, 4, 1. The value 6 is then
    // displayed as output.
    /// </summary>
    private int functionInputPointer;

    /// <summary>
    /// Number of cells available to functions. When a function is executed, an array of cells are allocated in
    // upper-addresses (eg., 1000-1999, 2000-2999, etc.) for usage.
    /// </summary>
    private final int functionSize = 300;

    /// <summary>
    /// Max number of iterations for a program or function to run. Can be custom specified within a function using
    // the syntax: @maxit=1234|function_code_here
    /// </summary>
    private int maxIterationCount;

    /// <summary>
    /// Storage memory value. Usually used to hold return values from function calls.
    /// </summary>
    private byte storage;

    /// <summary>
    /// Function return value. Set by using * command (instead of print . command).
    /// </summary>
    private Byte returnValue;

    /// <summary>
    /// Options for function behavior in the interpreter.
    /// </summary>
    private Function[] options = null;

    /// <summary>
    /// Number of instructions executed within the main program or the current function.
    /// </summary>
    private int ticks;

    /// <summary>
    /// Number of total instructions executed, including within functions.
    /// </summary>
    private int totalTicks;

    /// <summary>
    /// Flag to stop execution of the program.
    /// </summary>
    private boolean stop;

    /// <summary>
    /// Read-only access to the current data pointer index in memory.
    /// </summary>
    private int currentDataPointer;

    /// <summary>
    /// Read-only access to the current instruction pointer index.
    /// </summary>
    private int currentInstructionPointer;

    /// <summary>
    /// List of executed functions in the main program. Used for reference purposes by the GA to determine which
    // functions were executed in the program (not functions calling other functions).
    /// </summary>
    private Map<Character, Integer> executedFunctions = new HashMap<>();

    /// <summary>
    /// True if a function is currently running. False if the main program is running.
    /// </summary>
    public boolean isInsideFunction() {
        return functionCallStack.size() > 0;
    }

    /// <summary>
    /// True if currently inside a loop []. False otherwise. Note, check IsInsideFunction to tell if this is a loop
    // within a function or the main program.
    /// </summary>
    public boolean IsInsideLoop() {
        return currentCallStack.size() > 0;
    }

    /// <summary>
    /// The name of the currently executing function or null.
    /// </summary>
    public Character currentFunction() {
        if (isInsideFunction()) return functionCallStack.peek().getInstruction();
        else return null;
    }

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="programCode"></param>
    /// <param name="input">Function to call when input command (,) is executed.</param>
    /// <param name="output">Function to call when output command (.) is executed.</param>
    /// <param name="function">Callback handler to notify that a function is being executed: callback(instruction)
    // .</param>
    /// <param name="options">Additional interpreter options.</param>
    public Interpreter(String programCode, Supplier<Byte> input, Consumer<Byte> output) {
        init(programCode, input, output, null, null);
    }

    public Interpreter(String programCode, Supplier<Byte> input, Consumer<Byte> output,
                       Consumer<Character> function,
                       Function[] options) {
        init(programCode, input, output, function, options);
    }

    public void init(String programCode, Supplier<Byte> input, Consumer<Byte> output,
                     Consumer<Character> function,
                     Function[] options) {
        // Save the program code
        this.source = programCode.toCharArray();

        // Store the i/o delegates
        this.input = input;
        this.output = output;

        // Set any additional options.
        if (options != null) {
            this.options = options;
        }

        currentCallStack = callStack;

        // Create the instruction set for Basic Brainfuck.
        this.instructionSet.put('+', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer]++;
        });
        this.instructionSet.put('-', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer]--;
        });

        this.instructionSet.put('>', (v) -> {
            if (!exitLoop) this.dataPointer++;
        });
        this.instructionSet.put('<', (v) -> {
            if (!exitLoop) this.dataPointer--;
        });

        this.instructionSet.put('.', (v) -> {
            if (!exitLoop) this.output.accept(this.memory[this.dataPointer]);
        });

        // Prompt for input. If inside a function, pull input from parent memory, using the current
        // FunctionInputPointer. Each call for input advances the parent memory cell that gets read from, allowing
        // the passing of multiple values as input to a function.
        this.instructionSet.put(',', (v) -> {
            if (!exitLoop)
                memory[this.dataPointer] = isInsideFunction() ? this.memory[this.functionInputPointer++] : this.input.get();
        });

        this.instructionSet.put('[', (v) ->
        {
            if (!exitLoop && this.memory[this.dataPointer] == 0) {
                // Jump forward to the matching ] and exit this loop (skip over all inner loops).
                exitLoop = true;

                // Remember this instruction pointer, so when we get past all inner loops and finally pop this one
                // off the stack, we know we're done.
                exitLoopInstructionPointer = this.instructionPointer;
            }

            this.currentCallStack.push(this.instructionPointer);
        });
        this.instructionSet.put(']', (v) ->
        {
            Integer temp = this.currentCallStack.peek();
            this.currentCallStack.pop();

            if (!exitLoop) {
                this.instructionPointer = this.memory[this.dataPointer] != 0
                        ? temp - 1
                        : this.instructionPointer;
            } else {
                // Continue executing after loop.
                if (temp == exitLoopInstructionPointer) {
                    // We've finally exited the loop.
                    exitLoop = false;
                    exitLoopInstructionPointer = 0;
                }
            }
        });

        // Create the instruction set for Brainfuck Extended Type 3.
        this.instructionSet.put('0', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 0;
        });
        this.instructionSet.put('1', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 16;
        });
        this.instructionSet.put('2', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 32;
        });
        this.instructionSet.put('3', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 48;
        });
        this.instructionSet.put('4', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 64;
        });
        this.instructionSet.put('5', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 80;
        });
        this.instructionSet.put('6', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 96;
        });
        this.instructionSet.put('7', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = 112;
        });
        this.instructionSet.put('8', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 128;
        });
        this.instructionSet.put('9', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 144;
        });
        this.instructionSet.put('A', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 160;
        });
        this.instructionSet.put('B', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 176;
        });
        this.instructionSet.put('C', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 192;
        });
        this.instructionSet.put('D', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 208;
        });
        this.instructionSet.put('E', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 224;
        });
        this.instructionSet.put('F', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = (byte) 240;
        });
        this.instructionSet.put('*', (v) -> {
            if (!exitLoop) this.returnValue =
                    this.memory[this.dataPointer];
        });
        this.instructionSet.put('@', (v) ->
        {
            if (isInsideFunction()) {
                // Exit function.
                FunctionCallObj temp = functionCallStack.peek();
                functionCallStack.pop();

                // Restore the data pointer.
                this.dataPointer = temp.getDataPointer();

                    /*if (this.m_ReturnValue.HasValue)
                    {
                        this.m_Memory[this.m_DataPointer] = this.m_ReturnValue.Value;
                    }*/

                // Restore the call stack.
                this.currentCallStack = temp.getCallStack();
                // Restore exit loop status.
                this.exitLoop = temp.isExitLoop();
                // Restore exit loop instruction pointer.
                this.exitLoopInstructionPointer = temp.getExitLoopInstructionPointer();
                // Restore ticks.
                this.ticks = temp.getTicks();
                // Restore global storage.
                this.storage = this.returnValue != null ? this.returnValue : temp.getStorage();
                // Restore parent return value.
                this.returnValue = temp.getReturnValue();
                // Restore max iteraction count.
                this.maxIterationCount = temp.getMaxIterationCount();
                // Restore the instruction pointer.
                this.instructionPointer = temp.getInstructionPointer();
                // Restore function input pointer.
                this.functionInputPointer = temp.getFunctionInputPointer();
            } else {
                // Exit program.
                this.stop = true;
            }
        });
        this.instructionSet.put('$', (v) ->
        {
            if (!exitLoop) {
                // If we're inside a function, use the function's own global storage (separate from the main program).
                // However, if this is the last storage command in the function code, then use the main/calling-function
                // storage, to allow returning a value.
                if (isInsideFunction() && this.source[instructionPointer + 1] == '@') {
                    // Set function return value.
                    this.returnValue = this.memory[this.dataPointer];
                } else {
                    // Set global storage for this main program or function.
                    this.storage = this.memory[this.dataPointer];
                }
            }
        });

        this.instructionSet.put('!', (v) -> {
            if (!exitLoop) this.memory[this.dataPointer] = this.storage;
        });

        // Scan code for function definitions and store their starting memory addresses.
        ScanFunctions(programCode);

        // If we found any functions, create the instruction set for them.
        for (char inst = 'a'; inst < nextFunctionCharacter; inst++) {
            char instruction = inst; // closure
            this.instructionSet.put(instruction, (v) ->
            {
                if (!exitLoop) {
                    // Record a list of executed function names from the main program (not a function calling another
                    // function).
                    if (!isInsideFunction()) {
                        if (executedFunctions.containsKey(instruction)) {
                            executedFunctions.put(instruction, executedFunctions.get(instruction) + 1);
                        } else {
                            executedFunctions.put(instruction, 1);
                        }
                    }

                    if (function != null) {
                        // Notify caller of a function being executed.
                        function.accept(instruction);
                    }

                    // Store the current instruction pointer and data pointer before we move to the function.
                    FunctionCallObj functionCallObj = new FunctionCallObj();
                    functionCallObj.setInstructionPointer(this.instructionPointer)
                            .setDataPointer(this.dataPointer)
                            .setFunctionInputPointer(this.functionInputPointer)
                            .setCallStack(this.currentCallStack)
                            .setExitLoop(this.exitLoop)
                            .setExitLoopInstructionPointer(this.exitLoopInstructionPointer)
                            .setTicks(this.ticks)
                            .setInstruction(instruction)
                            .setStorage(this.storage)
                            .setReturnValue(this.returnValue)
                            .setMaxIterationCount(this.maxIterationCount);
                    this.functionCallStack.push(functionCallObj);

                    // Give the function a fresh call stack.
                    this.currentCallStack = new Stack<>();
                    this.exitLoop = false;
                    this.exitLoopInstructionPointer = 0;

                    // Initialize the function global storage.
                    this.storage = 0;
                    this.returnValue = null;

                    // Load options for this function.
                    FunctionInst functionOptions = functions.get(instruction);

                    // Set the function input pointer to the parent's starting memory. Calls for input (,) from within
                    // the function will read from parent's memory, each call advances the parent memory cell that gets
                    // read from. This allows passing multiple values to a function.
                    // Note, if we set the starting m_FunctionInputPointer to 0, functions will read from the first
                    // input
                    // position (0).
                    // If we set it to m_DataPointer, functions will read input from the current position in the parent
                    // memory (n). This is trickier for the GA to figure out, because it may have to downshift the
                    // memory
                    // back to 0 before calling the function so that the function gets all input. Setting this to 0
                    // makes
                    // it easier for the function to get the input.
                    this.functionInputPointer = functionOptions.isReadInputAtMemoryStart() ? 0 : this.dataPointer;

                    // Set the data pointer to the functions starting memory address.
                    this.dataPointer = functionSize * (instruction - 96); // each function gets a space of 1000 memory
                    // slots.

                    // Clear function memory.
                    Arrays.fill(this.memory, this.dataPointer, functionSize, (byte) 0);

                    // Set ticks to 0.
                    this.ticks = 0;

                    // Set the max iteration count for this function, if one was specified.
                    this.maxIterationCount = functionOptions.getMaxIterationCount() > 0 ?
                            functionOptions.getMaxIterationCount()
                            : this.maxIterationCount;

                    // Set the instruction pointer to the beginning of the function.
                    this.instructionPointer = functionOptions.getInstructionPointer();
                }
            });
        }
    }

    /// <summary>
    /// Run the program
    /// </summary>
    public void run() {
        run(0);
    }

    public void run(int maxInstructions) {
        ticks = 0;
        totalTicks = 0;
        stop = false;

        if (maxInstructions > 0) {
            RunLimited(maxInstructions);
        } else {
            RunUnlimited();
        }
    }

    /// <summary>
    /// Run the program with a maximum number of instructions before throwing an exception. Avoids infinite loops.
    /// </summary>
    /// <param name="maxInstructions">Max number of instructions to execute</param>
    private void RunLimited(int maxInstructions) {
        maxIterationCount = maxInstructions;

        // Iterate through the whole program source
        while (this.instructionPointer < this.source.length && !stop) {
            // Fetch the next instruction
            char instruction = this.source[this.instructionPointer];

            // See if that IS an instruction and execute it if so
            Consumer<Void> action;
            if (this.instructionSet.containsKey(instruction)) {
                // Yes, it was - execute
                action = this.instructionSet.get(instruction);
                action.accept(null);
            }

            // Next instruction
            this.instructionPointer++;

            // Have we exceeded the max instruction count?
            if (maxIterationCount > 0 && ticks >= maxIterationCount) {
                if (isInsideFunction()) {
                    // We're inside a function, but ran out of instructions. Exit the function, but continue.
                    if (this.instructionSet.containsKey('@')) {
                        action = this.instructionSet.get('@');
                        action.accept(null);
                        this.instructionPointer++;
                    }
                } else {
                    break;
                }
            }

            ticks++;
            totalTicks++;
        }
    }

    /// <summary>
    /// Run the program
    /// </summary>
    private void RunUnlimited() {
        // Iterate through the whole program source
        while (this.instructionPointer < this.source.length && !stop) {
            // Fetch the next instruction
            char instruction = this.source[this.instructionPointer];

            // See if that IS an instruction and execute it if so
            Consumer<Void> action;
            if (this.instructionSet.containsKey(instruction)) {
                // Yes, it was - execute

                action=this.instructionSet.get(instruction);
                action.accept(null);
            }

            // Next instruction
            this.instructionPointer++;

            ticks++;
            totalTicks++;
        }
    }

    /// <summary>
    /// Pre-scan the program code to record function instruction pointers.
    /// </summary>
    private void ScanFunctions(String source) {
        this.instructionPointer = source.indexOf('@');
        while (this.instructionPointer > -1 && this.instructionPointer < source.length() - 1 && !stop) {
            // Retrieve any settings for this function.
            Function functionDetail = options != null && options.length > functions.size() ?
                    options[functions.size()] : null;

            // Store the function.
            functions.put(nextFunctionCharacter++, new FunctionInst(this.instructionPointer, functionDetail));

            this.instructionPointer = source.indexOf('@', this.instructionPointer + 1);
        }

        this.instructionPointer = 0;
    }
}
