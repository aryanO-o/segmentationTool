package com.aryandadhich.segmantationtool.utils

class FrameMetadata(var width: Int = 0, var height: Int = 0, var rotation: Int = 0) {


    private fun FrameMetadata(width: Int, height: Int, rotation: Int) {
        this.width = width
        this.height = height
        this.rotation = rotation
    }

    /** Builder of [FrameMetadata].  */
    class Builder {
        private var width = 0
        private var height = 0
        private var rotation = 0
        fun setWidth(width: Int): Builder {
            this.width = width
            return this
        }

        fun setHeight(height: Int): Builder {
            this.height = height
            return this
        }

        fun setRotation(rotation: Int): Builder {
            this.rotation = rotation
            return this
        }

        fun build(): FrameMetadata {
            return FrameMetadata(width, height, rotation)
        }
    }
}
