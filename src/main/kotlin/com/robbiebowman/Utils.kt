package org.example.com.robbiebowman

object Utils {
    fun getRowsFromFile(path: String): List<String> {
        val resource = this::class.java.classLoader.getResource(path)
        val lines = resource!!.readText().split("\\n".toRegex())
        return lines
    }
}