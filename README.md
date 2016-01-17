# *ASM locals relocation bug*

When using [ASM](http://asm.ow2.org/)'s `RemappingClassAdapter`, even with an empty remapping, a method's locals can be relocated so that (seemingly) the first appearing in the bytecode sequence will get lower slots.

This is a serious issue for transforming logic that relies on local slot stability.

This issue is inherited f.e. by the [Gradle Shadow plugin](https://github.com/johnrengelman/shadow).

## Reproducing

This is the locals section of the `public V get()` method obtained through `javap -v src/main/resources/Val.class` (the class is produced by a [Quasar](https://github.com/puniverse/quasar/tree/v0.7.3) build.)

```
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
              70     118     3     i   I
              68     138     2 token   Ljava/lang/Object;
              59     181     1     s   Lco/paralleluniverse/strands/SimpleConditionSynchronizer;
             241       9     1     e   Lco/paralleluniverse/fibers/SuspendExecution;
              54     196     0  this   Lco/paralleluniverse/strands/dataflow/Val;
```

This class is instrumented by Quasar and uses additional undeclared locals in slots 4, 5 and 6.

Now let's run an empty remapping via `ASMTest` and write the resulting class:

```
./gradlew
```

This will output `Val-remapped.class` file in the project's dir. Here's the locals section obtained through `javap -v Val-remapped.class`:

```
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
              68     112     6     i   I
              65     137     5 token   Ljava/lang/Object;
              53     181     4     s   Lco/paralleluniverse/strands/SimpleConditionSynchronizer;
             236      10     4     e   Lco/paralleluniverse/fibers/SuspendExecution;
              47     199     0  this   Lco/paralleluniverse/strands/dataflow/Val;
```

Quasar's locals have been moved in slots 1, 2 and 3 by the (empty) remapping.
