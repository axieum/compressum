package me.axieum.java.compressum;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableArchive
{
    private final HashMap<File, String> ENTRIES;
    private final Format FORMAT;
    private final File OUTPUT;

    private CompletableFuture<File> task;
    public long processed = 0;
    private boolean cancelled = false;

    /**
     * Creates a new Completable Archive instance.
     *
     * @param compressum the compression instance for this to be based off
     */
    public CompletableArchive(Compressum compressum)
    {
        this.ENTRIES = compressum.getEntries();
        this.FORMAT = compressum.getFormat();
        this.OUTPUT = compressum.getOutput();
    }

    /**
     * Supply this completable with a future task on the specified executor.
     *
     * @param supplier the action be executed
     * @param executor the executor for the task to be executed on
     * @return this for chaining
     */
    public CompletableArchive supply(Supplier<File> supplier, Executor executor)
    {
        if (executor == null)
            return supply(supplier);

        this.task = CompletableFuture.supplyAsync(supplier, executor);
        return this;
    }

    /**
     * Supply this completable with a future task.
     *
     * @param supplier the action to be executed
     * @return this for chaining
     */
    public CompletableArchive supply(Supplier<File> supplier)
    {
        this.task = CompletableFuture.supplyAsync(supplier);
        return this;
    }

    /**
     * Retrieve the underlying completable future.
     *
     * @return the CompletableFuture instance to handle tasks
     */
    public CompletableFuture<File> getTask()
    {
        return task;
    }

    /**
     * Get the current progress of the task.
     *
     * @return percentage of archive entries processed
     */
    public double getProgress()
    {
        final int total = ENTRIES.size();
        return total == 0 ? 0 : processed / (double) total;
    }

    /**
     * Get whether the task was cancelled.
     *
     * @return true if the task was cancelled
     */
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * Cancel the task.
     */
    public void cancel()
    {
        this.cancelled = true;
    }

    public HashMap<File, String> getEntries()
    {
        return ENTRIES;
    }

    public Format getFormat()
    {
        return FORMAT;
    }

    public File getOutput()
    {
        return OUTPUT;
    }

    public CompletableArchive then(Consumer<? super File> action)
    {
        task.thenAccept(action);
        return this;
    }

    public CompletableArchive after(Runnable action)
    {
        task.thenRun(action);
        return this;
    }

    public CompletableArchive exceptionally(Function<Throwable, ? extends File> fn)
    {
        task = task.exceptionally(fn);
        return this;
    }

    public File get() throws InterruptedException, ExecutionException
    {
        return task.get();
    }
}
