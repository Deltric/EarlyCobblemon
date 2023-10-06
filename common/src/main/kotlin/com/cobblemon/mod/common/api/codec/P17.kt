/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.codec

import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.kinds.Applicative
import com.mojang.datafixers.kinds.K1

class P17<F : K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(
    private val t1: App<F, T1>,
    private val t2: App<F, T2>,
    private val t3: App<F, T3>,
    private val t4: App<F, T4>,
    private val t5: App<F, T5>,
    private val t6: App<F, T6>,
    private val t7: App<F, T7>,
    private val t8: App<F, T8>,
    private val t9: App<F, T9>,
    private val t10: App<F, T10>,
    private val t11: App<F, T11>,
    private val t12: App<F, T12>,
    private val t13: App<F, T13>,
    private val t14: App<F, T14>,
    private val t15: App<F, T15>,
    private val t16: App<F, T16>,
    private val t17: App<F, T17>
) {
    fun <R> apply(
        instance: Applicative<F, *>,
        function: Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>
    ): App<F, R> {
        return apply(instance, instance.point(function))
    }

    fun <R> apply(
        instance: Applicative<F, *>,
        function: App<F, Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>>
    ): App<F, R> {
        return instance.ap17(function, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
    }
}