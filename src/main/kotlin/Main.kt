package com.robbiebowman

fun main() {
    // Set up dependencies
    val dictionary = Dictionary()
    val titleChanger = TitleChanger(dictionary)
    val synopsisSynthesizer = SynopsisSynthesizer(System.getenv("CLAUDE_API_KEY"))
    val semanticAnaliser = SemanticAnaliser(System.getenv("OPEN_AI_KEY"))

    // Generate alternate titles for random film
    var title = ""
    var theChosenOne: CandidateTitle? = null
    var titles: List<CandidateTitle>
    while (theChosenOne == null) {
        title = getRandomFilm()
        titles = titleChanger.getCandidateTitles(title).toList()
        if (titles.isEmpty()) {
            println("No good candidates found for: $title")
            continue
        }
        println("Chose: $title")
        val qualifiedNewTitles = semanticAnaliser.filterOutSimilarStrings(titles)

        // Pick one of the titles and generate a blurb for it
        if (qualifiedNewTitles.isEmpty()) {
            continue
        }
        theChosenOne = qualifiedNewTitles.random()
        println(theChosenOne.changedTitle)
    }
    val response = synopsisSynthesizer.generateSynopsis(theChosenOne)
    println(response.blurb)
}

fun getRandomFilm(): String {
    return Utils.getRowsFromFile("top-1000-films.txt").random()
}