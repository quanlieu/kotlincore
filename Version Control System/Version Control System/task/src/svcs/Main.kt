package svcs

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

const val configHelp = "Please, tell me who you are."
const val addHelp = "Add a file to the index."
const val logHelp = "Show commit logs."
const val commitHelp = "Save changes."
const val wrongCmd = "is not a SVCS command."

const val helpPage = "These are SVCS commands:\n" +
        "config     Get and set a username.\n" +
        "add        Add a file to the index.\n" +
        "log        $logHelp\n" +
        "commit     $commitHelp\n" +
        "checkout   Restore a file.\n"

var userName = ""
val config = File("vcs${File.separator}config.txt")
val index = File("vcs${File.separator}index.txt")
val logFile = File("vcs${File.separator}log.txt")
val vcsDir = File("vcs")
val commitsDir = File("vcs${File.separator}commits")
val currentDir = File(".")

fun main(args: Array<String>) {
    init()
    if (args.isEmpty()) {
        println(helpPage)
    }
    if (args.size == 1) {
        when (val cmd = args[0]) {
            "--help" -> println(helpPage)
            "config" -> printUser()
            "add" -> printTrackedFiles()
            "log" -> printLog()
            "commit" -> println("Message was not passed.")
            "checkout" -> println("Commit id was not passed.")
            else -> println("'$cmd' $wrongCmd")
        }
    }

    if (args.size == 2) {
        when (val cmd = args[0]) {
            "--help" -> println(helpPage)
            "config" -> setUser(args[1])
            "add" -> addFile(args[1])
            "log" -> printLog()
            "commit" -> commitFiles(args[1])
            "checkout" -> checkout(args[1])
            else -> println("'$cmd' $wrongCmd")
        }
    }
}

fun checkout(hash: String) {
    val log = logFile.readLines()
    val commitHash = log.find { it.contains(hash) }?.split(" ")?.lastOrNull()
    if (commitHash != null) {
        copyFolder(File("vcs${File.separator}commits${File.separator}$commitHash"), currentDir)
        println("Switched to commit $commitHash.")
    } else {
        println("Commit does not exist.")
    }
}

fun copyFolder(source: File, destination: File) {
    if (!source.exists()) {
        throw IOException("Source folder does not exist")
    }

    if (!destination.exists()) {
        destination.mkdir()
    }

    for (file in source.listFiles()) {
        val target = File(destination.absolutePath + File.separator + file.name)
        if (file.isDirectory) {
            copyFolder(file, target)
        }
        else {
            Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

fun commitFiles(commitMsg: String) {
    val addedFile = index.readLines()
    val currentHash = generateSha256Hash(addedFile)
    val lastCommitHash = getLastCommitHash(logFile.readLines())
    if (lastCommitHash == currentHash || addedFile.isEmpty()) {
        println("Nothing to commit.")
    } else {
        copyFiles(addedFile, "vcs${File.separator}commits${File.separator}$currentHash")
        logFile.appendText("$commitMsg\n")
        logFile.appendText("Author: $userName\n")
        logFile.appendText("commit $currentHash\n\n")
        println("Changes are committed.")
    }
}

fun getLastCommitHash(list: List<String>): String? {
    return list.lastOrNull { it.contains("commit", true) }?.split(" ")?.lastOrNull()
}

fun printLog() {
    val log = logFile.readLines()
    if (log.isEmpty()) {
        println("No commits yet.")
    } else {
        log.asReversed().forEach { println(it) }
    }
}

fun printTrackedFiles() {
    val files = index.readLines()
    if (files.isEmpty()) {
        println(addHelp)
    } else {
        println("Tracked files:")
        files.forEach { println(it) }
    }
}

fun addFile(s: String) {
    if (File(s).exists()) {
        index.appendText("${s}\n")
        println("The file '${s}' is tracked.\n")
    } else {
        println("Can't find '${s}'.")
    }
}

fun printUser() {
    if (userName == "") {
        println(configHelp)
    } else {
        println("The username is ${userName}.")
    }
}

fun setUser(s: String) {
    config.writeText(s)
    println("The username is ${s}.")
}

fun init() {
    if (!vcsDir.exists()) vcsDir.mkdir()
    if (!commitsDir.exists()) commitsDir.mkdir()
    if (!config.exists()) config.createNewFile()
    if (!index.exists()) index.createNewFile()
    if (!logFile.exists()) logFile.createNewFile()

    userName = config.readText()
}

// function to read contents of a file to a byte array
fun readFileToByteArray(filename: String): ByteArray {
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    File(filename).inputStream().use { input ->
        input.copyTo(byteArrayOutputStream)
    }
    return byteArrayOutputStream.toByteArray()
}

// function to generate SHA-256 hash of contents of a list of files
fun generateSha256Hash(filenames: List<String>): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    filenames.forEach { filename ->
        val buffer = readFileToByteArray(filename)
        messageDigest.update(buffer)
    }
    val hash = messageDigest.digest()
    return hash.joinToString("") { "%02x".format(it) }
}

fun copyFiles(list: List<String>, destinationFolder: String) {
    val destDir = File(destinationFolder)
    // create destination directory if it doesn't exist
    if (!destDir.exists()) {
        destDir.mkdir()
    }
    // iterate over the list of file names and copy each file to the destination directory
    for (file in list) {
        val sourceFile = File(file)
        val destFile = File(destDir, sourceFile.name)
        // copy the file
        sourceFile.copyTo(destFile, true)
    }
}
