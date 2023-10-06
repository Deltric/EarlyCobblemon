/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.codec

import com.mojang.datafixers.util.*
import java.util.function.BiFunction
import java.util.function.Function


interface Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17): R

    fun curry(): Function<T1, Function16<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function { t1: T1 ->
            Function16 { t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry2(): BiFunction<T1, T2, Function15<T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return BiFunction { t1: T1, t2: T2 ->
            Function15 { t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry3(): Function3<T1, T2, T3, Function14<T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function3 { t1: T1, t2: T2, t3: T3 ->
            Function14 { t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry4(): Function4<T1, T2, T3, T4, Function13<T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function4 { t1: T1, t2: T2, t3: T3, t4: T4 ->
            Function13 { t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry5(): Function5<T1, T2, T3, T4, T5, Function12<T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function5 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 ->
            Function12 { t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry6(): Function6<T1, T2, T3, T4, T5, T6, Function11<T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function6 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 ->
            Function11 { t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry7(): Function7<T1, T2, T3, T4, T5, T6, T7, Function10<T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function7 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 ->
            Function10 { t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry8(): Function8<T1, T2, T3, T4, T5, T6, T7, T8, Function9<T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function8 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 ->
            Function9 { t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry9(): Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Function8<T10, T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function9 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 ->
            Function8 { t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 ->
                apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
            }
        }
    }

    fun curry10(): Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Function7<T11, T12, T13, T14, T15, T16, T17, R>> {
        return Function10 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10 ->
            Function7 { t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry11(): Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Function6<T12, T13, T14, T15, T16, T17, R>> {
        return Function11 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11 ->
            Function6 { t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry12(): Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Function5<T13, T14, T15, T16, T17, R>> {
        return Function12 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12 ->
            Function5 { t13: T13, t14: T14, t15: T15, t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry13(): Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, Function4<T14, T15, T16, T17, R>> {
        return Function13 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13 ->
            Function4 { t14: T14, t15: T15, t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry14(): Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, Function3<T15, T16, T17, R>> {
        return Function14 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14 ->
            Function3 { t15: T15, t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry15(): Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, BiFunction<T16, T17, R>> {
        return Function15 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15 ->
            BiFunction { t16: T16, t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }

    fun curry16(): Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, Function<T17, R>> {
        return Function16 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16 ->
            Function { t17: T17 -> apply( t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) }
        }
    }
}
