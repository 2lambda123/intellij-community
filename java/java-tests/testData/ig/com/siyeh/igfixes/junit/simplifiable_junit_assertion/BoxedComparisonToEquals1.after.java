// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoxedComparisonToEquals1 {
    void test(Boolean a, boolean b) {
        assertEquals((boolean) a, b);
    }
}