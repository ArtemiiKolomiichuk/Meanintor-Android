package com.example.practiceeng
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject;
import java.lang.Exception

class APIHandler{
    companion object {
        fun print(word: String, api : DictionaryAPI) {
            if(api == DictionaryAPI.WordsAPI) {
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
                println(response.body!!.string())
            }
            else if (api == DictionaryAPI.FreeDictionaryAPI){
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                println(response.body!!.string())
            }
        }

        fun getCards(word: String, api : DictionaryAPI) : Array<WordCard>{

            if(api == DictionaryAPI.WordsAPI) {
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
                val jsonObject = JSONObject(responseString)
                val wordString = jsonObject.optString("word")
                val pronunciation = jsonObject.getJSONObject("pronunciation")
                val phonetic = pronunciation.optString("all")
                val word = Word(wordString, phonetic)
                var cards = arrayOf<WordCard>()
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
                    }catch (e : Exception){}

                    try {
                        val antonyms = result.getJSONArray("antonyms")
                        for (j in 0 until antonyms.length()) {
                            card.antonyms += antonyms.getString(j)
                        }
                    }catch (e : Exception){}
                    try{
                        val examples = result.getJSONArray("examples")
                        for (j in 0 until examples.length()) {
                            card.examples += examples.getString(j)
                        }
                    }catch (e : Exception){}
                    cards += card
                }
                return cards
            }
            else{
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                var responseString = response.body!!.string()
                responseString = responseString.subSequence(1, responseString.length - 1).toString()
                val jsonObject = JSONObject(responseString)
                val wordString = jsonObject.optString("word")
                val phonetic = jsonObject.optString("phonetic")

                val word = Word(wordString, phonetic)

                var cards = arrayOf<WordCard>()
                val meanings = jsonObject.getJSONArray("meanings")
                for(i in 0 until meanings.length()){
                    val meaning = meanings.getJSONObject(i)
                    val partOfSpeech = meaning.optString("partOfSpeech")
                    val definitions = meaning.getJSONArray("definitions")
                    for(j in 0 until definitions.length()){
                        val definition = definitions.getJSONObject(j)
                        val definitionString = definition.optString("definition")
                        val example = definition.optString("example")
                        val synonyms = definition.optJSONArray("synonyms")
                        val antonyms = definition.optJSONArray("antonyms")
                        val card = WordCard(word, partOfSpeech, definitionString)
                        card.examples += example
                        if(synonyms != null){
                            for(k in 0 until synonyms.length()){
                                card.synonyms += (synonyms.getString(k))
                            }
                        }
                        if(antonyms != null){
                            for(k in 0 until antonyms.length()){
                                card.antonyms += (antonyms.getString(k))
                            }
                        }
                        cards += card
                    }
                }
                return cards
            }

        }
    }


}


