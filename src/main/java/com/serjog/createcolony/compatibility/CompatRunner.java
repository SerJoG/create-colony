package com.serjog.createcolony.compatibility;

import java.util.function.Supplier;

public interface CompatRunner {
    void run(Supplier<Runnable> function);
}
