package com.example.practiceeng

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class APIHandler{
    companion object {
        /**
         * RapidAPI application key for WordsAPI and XFEnglishDictionary
         */
        private val applicationKey : String = TODO()

        /**
         * Get an array of [WordCard]s for a given word,
         * may return empty array if word is not found
         * @param word the word to get cards for
         * @param api the api to use
         * @return an array of [WordCard]s
         */
        fun getCards(word: String, api : DictionaryAPI = DictionaryAPI.ALL) : Array<WordCard>{
            when (api) {
                DictionaryAPI.WordsAPI -> {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://wordsapiv1.p.rapidapi.com/words/$word")
                        .get()
                        .addHeader(
                            "X-RapidAPI-Key",
                            applicationKey
                        )
                        .addHeader("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com")
                        .build()
                    val response = client.newCall(request).execute()
                    val responseString = response.body!!.string()
                    if(responseString.contains("\"message\":\"word not found\"")){
                        return arrayOf()
                    }
                    val jsonObject = JSONObject(responseString)
                    var word : Word
                    val wordString = jsonObject.optString("word")
                    try {
                        var phonetics = arrayOf<String>()
                        val pronunciation = jsonObject.getJSONObject("pronunciation")
                        if(pronunciation.toString().contains("\"all\":\"")) {
                            phonetics += "(General) ${pronunciation.optString("all")}"
                        }
                        if(pronunciation.toString().contains("\"noun\":\"")){
                            phonetics += "(Noun) ${pronunciation.optString("noun")}"
                        }
                        if(pronunciation.toString().contains("\"verb\":\"")) {
                            phonetics += "(Verb) ${pronunciation.optString("verb")}"
                        }
                        word = Word(wordString, phonetics)
                    }catch (e: Exception){
                        word = Word(wordString)
                    }
                    var cards = arrayOf<WordCard>()
                    try {
                        val results = jsonObject.getJSONArray("results")
                        for (i in 0 until results.length()) {
                            val result = results.getJSONObject(i)
                            val partOfSpeech = result.optString("partOfSpeech")
                            val definition = result.optString("definition")
                            val card = WordCard(
                                partOfSpeech,
                                definition,
                                word = word,
                                folderID = null
                            )
                            try {
                                val synonyms = result.getJSONArray("synonyms")
                                for (j in 0 until synonyms.length()) {
                                    card.synonyms += synonyms.getString(j)
                                }
                            } catch (_: Exception) {}
                            try {
                                val antonyms = result.getJSONArray("antonyms")
                                for (j in 0 until antonyms.length()) {
                                    card.antonyms += antonyms.getString(j)
                                }
                            } catch (_: Exception) {}
                            try {
                                val examples = result.getJSONArray("examples")
                                for (j in 0 until examples.length()) {
                                    card.examples += examples.getString(j)
                                }
                            } catch (_: Exception) {}
                            cards += card
                        }
                    }catch (_: Exception){}
                    return cards
                }
                DictionaryAPI.FreeDictionaryAPI -> {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                        .get()
                        .build()
                    val response = client.newCall(request).execute()
                    var responseString = response.body!!.string()
                    if(responseString.contains("No Definitions Found")){
                        return arrayOf()
                    }
                    responseString = "{\"array\":$responseString}"
                    val jsonObject = JSONObject(responseString)
                    val jsonArray = jsonObject.getJSONArray("array")

                    val wordString = jsonArray.getJSONObject(0).optString("word")
                    val phonetic = jsonArray.getJSONObject(0).optString("phonetic")
                    val word = Word(wordString, Array(3){phonetic})

                    var cards = arrayOf<WordCard>()
                    for(k in 0 until jsonArray.length()){
                        val meanings = jsonArray.getJSONObject(k).getJSONArray("meanings")
                        for(i in 0 until meanings.length()) {
                            val meaning = meanings.getJSONObject(i)
                            val partOfSpeech = meaning.optString("partOfSpeech")
                            val definitions = meaning.getJSONArray("definitions")
                            for (j in 0 until definitions.length()) {
                                val definition = definitions.getJSONObject(j)
                                val definitionString = definition.optString("definition")
                                val example = definition.optString("example")
                                val synonyms = definition.optJSONArray("synonyms")
                                val antonyms = definition.optJSONArray("antonyms")
                                val card = WordCard(
                                    partOfSpeech,
                                    definitionString,
                                    word = word,
                                    folderID = null
                                )
                                card.examples += example
                                if (synonyms != null) {
                                    for (k in 0 until synonyms.length()) {
                                        card.synonyms += (synonyms.getString(k))
                                    }
                                }
                                if (antonyms != null) {
                                    for (k in 0 until antonyms.length()) {
                                        card.antonyms += (antonyms.getString(k))
                                    }
                                }
                                cards += card
                            }
                        }
                    }
                    return cards
                }
                DictionaryAPI.XFEnglishDictionary ->{
                    val client = OkHttpClient()
                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body = "{\r\n    \"selection\": \"$word\"\r\n}".toRequestBody(mediaType)
                    val selection = word.split(" ")[0]
                    val request = Request.Builder()
                        .url("https://xf-english-dictionary1.p.rapidapi.com/v1/dictionary?selection=$selection&synonyms=true&audioFileLinks=true&pronunciations=true&relatedWords=false&antonyms=true")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("X-RapidAPI-Key", applicationKey)
                        .addHeader("X-RapidAPI-Host", "xf-english-dictionary1.p.rapidapi.com")
                        .build()

                    val response: Response
                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit(Callable {
                        client.newCall(request).execute()
                    })
                    response = future.get(4000, java.util.concurrent.TimeUnit.MILLISECONDS)

                    val responseString = response.body!!.string()
                    println(responseString)
                    if(responseString == "{}"){
                        return arrayOf()
                    }
                    val jsonObject = JSONObject(responseString)
                    val items = jsonObject.getJSONArray("items")
                    var cards = arrayOf<WordCard>()
                    for (i in 0 until items.length()){
                        try {
                            val phrases = items.getJSONObject(i).optJSONArray("phrases")
                            for(p in 0 until phrases.length()){
                                val phrase = phrases.getJSONObject(p)
                                val phraseString = phrase.optString("phrase")
                                val partOfSpeech = phrase.optString("partOfSpeech")
                                val word = Word(phraseString)
                                val definitions = phrase.getJSONArray("definitions")
                                for(d in 0 until definitions.length()){
                                    val definition = definitions.getJSONObject(d)
                                    val definitionString = definition.optString("definition")
                                    val card = WordCard(
                                        partOfSpeech,
                                        definitionString,
                                        word = word,
                                        folderID = null
                                    )
                                    val examples = definition.optJSONArray("examples")
                                    //val synonyms = definition.optJSONArray("synonyms")
                                    //val antonyms = definition.optJSONArray("antonyms")
                                    for(e in 0 until examples.length()){
                                        card.examples += examples.getString(e)
                                    }
                                    cards += card
                                }
                            }
                        }catch (e: Exception){ println("phrase: $e")}
                    }

                    for (i in 0 until items.length()){
                        try {
                            val wordString = items.getJSONObject(i).optString("word")
                            val partOfSpeech = items.getJSONObject(i).optString("partOfSpeech")
                            val word = Word(wordString)
                            var itemSynonymsString = arrayOf<String>()
                            try {
                                val itemSynonyms = items.getJSONObject(i).optJSONArray("synonyms")
                                for (s in 0 until itemSynonyms.length()){
                                    itemSynonymsString += itemSynonyms.getString(s)
                                }
                            }catch (_: Exception){}
                            var itemAntonymsString = arrayOf<String>()
                            try {
                                val itemAntonyms = items.getJSONObject(i).optJSONArray("antonyms")
                                for (a in 0 until itemAntonyms.length()){
                                    itemAntonymsString += itemAntonyms.getString(a)
                                }
                            }catch (_: Exception){}
                            val definitions = items.getJSONObject(i).getJSONArray("definitions")
                            for (d in 0 until definitions.length()){
                                val definition = definitions.getJSONObject(d)
                                val definitionString = definition.optString("definition")
                                val card = WordCard(
                                    partOfSpeech,
                                    definitionString,
                                    word = word,
                                    folderID = null
                                )
                                try {
                                    val examples = definition.optJSONArray("examples")
                                    for(e in 0 until examples.length()){
                                        card.examples += examples.getString(e)
                                    }
                                }catch (_: Exception){}
                                try {
                                    val synonyms = definition.optJSONArray("synonyms")
                                    for (s in 0 until synonyms.length()){
                                        card.synonyms += synonyms.getString(s)
                                    }
                                }catch (_: Exception){}
                                try {
                                    val antonyms = definition.optJSONArray("antonyms")
                                    for (a in 0 until antonyms.length()) {
                                        card.antonyms += antonyms.getString(a)
                                    }
                                }catch (_: Exception){}
                                if(itemSynonymsString.isNotEmpty()) card.synonyms += itemSynonymsString
                                if(itemAntonymsString.isNotEmpty()) card.antonyms += itemAntonymsString
                                cards += card
                            }
                        }catch (e: Exception){
                            println(e)
                        }
                    }
                    try {
                        var phonetics = arrayOf<String>()
                        var audioLinks = arrayOf<String>()
                        val pronunciations = jsonObject.getJSONArray("pronunciations")
                        val entries = pronunciations.getJSONObject(0).getJSONArray("entries")
                        for(e in 0 until entries.length()){
                            val entry = entries.getJSONObject(e)
                            val entryString = entry.optString("entry")
                            if(entryString == word){
                                val textual = entry.optJSONArray("textual")
                                for(t in 0 until textual.length()){
                                    val pronunciation = textual.getJSONObject(t).optString("pronunciation")
                                    phonetics += pronunciation
                                }
                                val audioFiles = entry.optJSONArray("audioFiles")
                                for(a in 0 until audioFiles.length()){
                                    val audioFile = audioFiles.getJSONObject(a).optString("link")
                                    audioLinks += audioFile
                                }
                            }
                        }
                        cards[0].word().phonetics = phonetics
                        cards[0].word().audioLinks = audioLinks
                    }catch (_: Exception){}
                    return cards
                }
                DictionaryAPI.ALL ->{
                    val cards : MutableList<WordCard>
                    try {
                        cards = (getCards(word, DictionaryAPI.XFEnglishDictionary) + getCards(word, DictionaryAPI.WordsAPI)).toMutableList()
                    }catch (e: Exception) {
                        Log.e("APIHandler(ALL)", "Error: $e")
                        return arrayOf()
                    }
                    //get pronunciation from WordAPI
                    val theWord : Word = if(cards.isNotEmpty()) cards[cards.size - 1].word() else return arrayOf()
                    //try to get pronunciation from XFEnglishDictionary
                    if(cards.isNotEmpty() && cards[0].word().phonetics.isNotEmpty() && cards[0].word().phonetics[0].contains("/")){
                        val wordWord = cards[0].word()
                        val temp = Word(wordWord.word)
                        temp.audioLinks = arrayOf()
                        for (i in 0 until wordWord.audioLinks.size){
                            if(wordWord.audioLinks[i].lowercase().contains("us")){
                                temp.audioLinks += arrayOf("US", wordWord.audioLinks[i])
                            }else if (wordWord.audioLinks[i].lowercase().contains("uk")){
                                temp.audioLinks += arrayOf("UK", wordWord.audioLinks[i])
                            }else if (wordWord.audioLinks[i].lowercase().contains("au")) {
                                temp.audioLinks += arrayOf("AU", wordWord.audioLinks[i])
                            }
                        }
                        theWord.audioLinks = temp.audioLinks
                        temp.phonetics = arrayOf()
                        for(i in 0 until wordWord.phonetics.size){
                            if(wordWord.phonetics[i].contains("US") || wordWord.phonetics[i].contains("General American")){
                                val phonetics = wordWord.phonetics[i].replaceFirst("/", "&*").substringAfter("&*").substringBefore("/")
                                temp.phonetics += arrayOf("(US) $phonetics")
                            }else if (wordWord.phonetics[i].contains("UK")){
                                val phonetics = wordWord.phonetics[i].replaceFirst("/", "&*").substringAfter("&*").substringBefore("/")
                                temp.phonetics += arrayOf("(UK) $phonetics")
                            }else if (wordWord.phonetics[i].contains("AU") || wordWord.phonetics[i].lowercase().contains("australia")) {
                                val phonetics = wordWord.phonetics[i].replaceFirst("/", "&*")
                                    .substringAfter("&*").substringBefore("/")
                                temp.phonetics += arrayOf("(AU) $phonetics")
                            }else if (wordWord.phonetics[i].contains("New Zealand")){
                                val phonetics = wordWord.phonetics[i].replaceFirst("/", "&*").substringAfter("&*").substringBefore("/")
                                temp.phonetics += arrayOf("(NZ) $phonetics")
                            }else if (wordWord.phonetics[i].contains("Received Pronunciation")) {
                                val phonetics = wordWord.phonetics[i].replaceFirst("/", "&*").substringAfter("&*").substringBefore("/")
                                temp.phonetics += arrayOf("(General) $phonetics")
                            }
                        }
                        if(temp.phonetics.isNotEmpty()){
                            theWord.phonetics = temp.phonetics
                        }
                    }

                    var i = 0
                    while (i < cards.size){
                        if(cards[i].wordString() != word){
                            cards.removeAt(i)
                        }else{
                            i++
                        }
                    }
                    for(c in cards){
                        c.word = theWord
                        c.definition = c.definition.replace("<[^>]*>".toRegex(), "")
                        for(e in 0 until c.examples.size){
                            c.examples[e] = c.examples[e].replace("<[^>]*>".toRegex(), "")
                        }
                        for(s in 0 until c.synonyms.size){
                            c.synonyms[s] = c.synonyms[s].replace("<[^>]*>".toRegex(), "")
                        }
                        for(a in 0 until c.antonyms.size){
                            c.antonyms[a] = c.antonyms[a].replace("<[^>]*>".toRegex(), "")
                        }
                    }
                    return cards.toTypedArray()
                }
            }
        }
    }
}


