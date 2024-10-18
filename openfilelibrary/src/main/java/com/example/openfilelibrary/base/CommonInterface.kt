package com.example.openfilelibrary.base

/**
 * @author zyju
 * @date 2024/9/11 8:48
 */
interface ICell<T> {
    fun cell(cell: T)
    interface ICell2<T, B> {
        fun cell(cell: T, cell1: B)
    }

    interface ICell3<T, B, S> {
        fun cell(cell: T, cell1: B, s: S)
    }
}