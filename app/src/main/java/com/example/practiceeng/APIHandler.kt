package com.example.practiceeng
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject;
import java.lang.Exception

class APIHandler{
    companion object {
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
                            "364859fdf5msh35332022afd4d68p15abf5jsn8e6c3ef6b1d0"
                        )
                        .addHeader("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com")
                        .build()
                    val response = client.newCall(request).execute()
                    var responseString = response.body!!.string()
                    if(responseString.contains("\"message\":\"word not found\"")){
                        return arrayOf<WordCard>()
                    }
                    val jsonObject = JSONObject(responseString)
                    var word : Word;
                    val wordString = jsonObject.optString("word")
                    try {
                        var phonetics = arrayOf("")
                        val pronunciation = jsonObject.getJSONObject("pronunciation")
                        phonetics += if(pronunciation.toString().contains("\"all\":\"")){
                            pronunciation.optString("all")
                        }else {""}
                        phonetics += if(pronunciation.toString().contains("\"noun\":\"")) {
                            pronunciation.optString("noun")
                        }else {""}
                        phonetics += if(pronunciation.toString().contains("\"verb\":\"")) {
                            pronunciation.optString("verb")
                        }else {""}
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
                            val card = WordCard(word, partOfSpeech, definition)
                            try {
                                val synonyms = result.getJSONArray("synonyms")
                                for (j in 0 until synonyms.length()) {
                                    card.synonyms += synonyms.getString(j)
                                }
                            } catch (e: Exception) {}
                            try {
                                val antonyms = result.getJSONArray("antonyms")
                                for (j in 0 until antonyms.length()) {
                                    card.antonyms += antonyms.getString(j)
                                }
                            } catch (e: Exception) {}
                            try {
                                val examples = result.getJSONArray("examples")
                                for (j in 0 until examples.length()) {
                                    card.examples += examples.getString(j)
                                }
                            } catch (e: Exception) {}
                            cards += card
                        }
                    }catch (e: Exception){}
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
                                val card = WordCard(word, partOfSpeech, definitionString)
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
                DictionaryAPI.ALL ->{
                    return getCards(word, DictionaryAPI.WordsAPI) + getCards(word, DictionaryAPI.FreeDictionaryAPI)
                }
            }
        }
    }


}


