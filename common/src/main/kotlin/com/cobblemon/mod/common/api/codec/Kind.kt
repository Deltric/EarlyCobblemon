/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.codec

import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.kinds.K1
import com.mojang.datafixers.kinds.Kind1

fun <F : K1, Mu: Kind1.Mu, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Kind1<F, Mu>.groupMany(
    t1: App<F, T1>,
    t2: App<F, T2>,
    t3: App<F, T3>,
    t4: App<F, T4>,
    t5: App<F, T5>,
    t6: App<F, T6>,
    t7: App<F, T7>,
    t8: App<F, T8>,
    t9: App<F, T9>,
    t10: App<F, T10>,
    t11: App<F, T11>,
    t12: App<F, T12>,
    t13: App<F, T13>,
    t14: App<F, T14>,
    t15: App<F, T15>,
    t16: App<F, T16>,
    t17: App<F, T17>
) = P17(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)