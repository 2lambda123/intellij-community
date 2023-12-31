// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.codeStyle.extractor.values;

import com.intellij.psi.codeStyle.extractor.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Gens extends ValuesExtractionResultImpl {

  public Gens(@NotNull List<Value> values) {
    super(values);
  }

  public Gens(@NotNull Gens gens) {
    this(gens.copy().getValues());
  }

  public Gens mutate(int averageMutationCount) {
    if (averageMutationCount > 0) {
      // with mutation

      int commonMutagen = myValues.stream().mapToInt(Value::getMutagenFactor).sum();

      for (Value value : myValues) {
        if (Utils.getRandomLess(commonMutagen) < value.getMutagenFactor() * averageMutationCount) {
          final Object[] possibleValues = value.getPossibleValues();
          value.value = possibleValues[Utils.getRandomLess(possibleValues.length)];
        }
      }
    }
    return this;
  }

  public Gens dropToInitial() {
    for (Value value : myValues) {
      final Object[] possibleValues = value.getPossibleValues();
      if (possibleValues.length > 0) {
        value.value = possibleValues[0];
      }
    }
    return this;
  }

  public @NotNull Gens copy() {
    final ArrayList<Value> values = new ArrayList<>();
    for (Value value : myValues) {
      values.add(new Value(value));
    }
    return new Gens(values);
  }


  public Gens diff(Gens newGens) {
    final List<Value> newValues = newGens.getValues();
    final int size = myValues.size();
    assert size == newValues.size();
    final List<Value> diff = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      final Value value = myValues.get(i);
      final Value newValue = newValues.get(i);
      assert value.name.equals(newValue.name);
      if (!value.value.equals(newValue.value)) {
        diff.add(new Value(value.name, value.value + "->" + newValue.value, value.serializer, value.kind));
      }
    }
    return new Gens(diff);
  }

  public static @NotNull Gens breed(@NotNull Gens p1, @NotNull Gens p2, int averageMutationCount) {
    final int size = p1.myValues.size();
    assert size == p2.myValues.size();

    // Crossover!
    final int crossover = size / 2;//FUtils.getRandomLess(size - 6) + 3;

    final List<Value> values = new ArrayList<>(size);
    for (int i = 0; i < size; ++i) {
      final Value value1 = p1.myValues.get(i);
      final Value value2 = p2.myValues.get(i);
      if (value1.kind == Value.VAR_KIND.INDENT) {
        values.add(new Value(value1));
      }
      else if (value1.kind == Value.VAR_KIND.BRACE_STYLE) {
        values.add(new Value(value2));
      }
      else if ((i & 0x1) == 1) {
        values.add(new Value(value1));
      }
      else {
        values.add(new Value(value2));
      }
    }
    return new Gens(values).mutate(averageMutationCount);
  }

  public void copyFrom(Gens gens) {
    if (myValues == gens.myValues) {
      return;
    }
    myValues.clear();
    myValues.addAll(gens.myValues);
  }
}
