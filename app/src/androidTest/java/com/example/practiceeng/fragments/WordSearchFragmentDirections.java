package com.example.practiceeng.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import com.example.practiceeng.R;
import java.io.Serializable;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.UUID;

public class WordSearchFragmentDirections {
  private WordSearchFragmentDirections() {
  }

  @NonNull
  public static AddWordCard addWordCard(@NonNull String name, @Nullable String partOfSpeech,
      @Nullable String definition, @Nullable String[] example, @Nullable String[] synonyms,
      @Nullable String[] antonyms, @Nullable UUID folder, @Nullable UUID cardID) {
    return new AddWordCard(name, partOfSpeech, definition, example, synonyms, antonyms, folder, cardID);
  }

  public static class AddWordCard implements NavDirections {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    private AddWordCard(@NonNull String name, @Nullable String partOfSpeech,
        @Nullable String definition, @Nullable String[] example, @Nullable String[] synonyms,
        @Nullable String[] antonyms, @Nullable UUID folder, @Nullable UUID cardID) {
      if (name == null) {
        throw new IllegalArgumentException("Argument \"name\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("name", name);
      this.arguments.put("partOfSpeech", partOfSpeech);
      this.arguments.put("definition", definition);
      this.arguments.put("example", example);
      this.arguments.put("synonyms", synonyms);
      this.arguments.put("antonyms", antonyms);
      this.arguments.put("folder", folder);
      this.arguments.put("cardID", cardID);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setName(@NonNull String name) {
      if (name == null) {
        throw new IllegalArgumentException("Argument \"name\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("name", name);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setPartOfSpeech(@Nullable String partOfSpeech) {
      this.arguments.put("partOfSpeech", partOfSpeech);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setDefinition(@Nullable String definition) {
      this.arguments.put("definition", definition);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setExample(@Nullable String[] example) {
      this.arguments.put("example", example);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setSynonyms(@Nullable String[] synonyms) {
      this.arguments.put("synonyms", synonyms);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setAntonyms(@Nullable String[] antonyms) {
      this.arguments.put("antonyms", antonyms);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setFolder(@Nullable UUID folder) {
      this.arguments.put("folder", folder);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public AddWordCard setCardID(@Nullable UUID cardID) {
      this.arguments.put("cardID", cardID);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public Bundle getArguments() {
      Bundle __result = new Bundle();
      if (arguments.containsKey("name")) {
        String name = (String) arguments.get("name");
        __result.putString("name", name);
      }
      if (arguments.containsKey("partOfSpeech")) {
        String partOfSpeech = (String) arguments.get("partOfSpeech");
        __result.putString("partOfSpeech", partOfSpeech);
      }
      if (arguments.containsKey("definition")) {
        String definition = (String) arguments.get("definition");
        __result.putString("definition", definition);
      }
      if (arguments.containsKey("example")) {
        String[] example = (String[]) arguments.get("example");
        __result.putStringArray("example", example);
      }
      if (arguments.containsKey("synonyms")) {
        String[] synonyms = (String[]) arguments.get("synonyms");
        __result.putStringArray("synonyms", synonyms);
      }
      if (arguments.containsKey("antonyms")) {
        String[] antonyms = (String[]) arguments.get("antonyms");
        __result.putStringArray("antonyms", antonyms);
      }
      if (arguments.containsKey("folder")) {
        UUID folder = (UUID) arguments.get("folder");
        if (Parcelable.class.isAssignableFrom(UUID.class) || folder == null) {
          __result.putParcelable("folder", Parcelable.class.cast(folder));
        } else if (Serializable.class.isAssignableFrom(UUID.class)) {
          __result.putSerializable("folder", Serializable.class.cast(folder));
        } else {
          throw new UnsupportedOperationException(UUID.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
        }
      }
      if (arguments.containsKey("cardID")) {
        UUID cardID = (UUID) arguments.get("cardID");
        if (Parcelable.class.isAssignableFrom(UUID.class) || cardID == null) {
          __result.putParcelable("cardID", Parcelable.class.cast(cardID));
        } else if (Serializable.class.isAssignableFrom(UUID.class)) {
          __result.putSerializable("cardID", Serializable.class.cast(cardID));
        } else {
          throw new UnsupportedOperationException(UUID.class.getName() + " must implement Parcelable or Serializable or must be an Enum.");
        }
      }
      return __result;
    }

    @Override
    public int getActionId() {
      return R.id.add_word_card;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getName() {
      return (String) arguments.get("name");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public String getPartOfSpeech() {
      return (String) arguments.get("partOfSpeech");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public String getDefinition() {
      return (String) arguments.get("definition");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public String[] getExample() {
      return (String[]) arguments.get("example");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public String[] getSynonyms() {
      return (String[]) arguments.get("synonyms");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public String[] getAntonyms() {
      return (String[]) arguments.get("antonyms");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public UUID getFolder() {
      return (UUID) arguments.get("folder");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public UUID getCardID() {
      return (UUID) arguments.get("cardID");
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
          return true;
      }
      if (object == null || getClass() != object.getClass()) {
          return false;
      }
      AddWordCard that = (AddWordCard) object;
      if (arguments.containsKey("name") != that.arguments.containsKey("name")) {
        return false;
      }
      if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
        return false;
      }
      if (arguments.containsKey("partOfSpeech") != that.arguments.containsKey("partOfSpeech")) {
        return false;
      }
      if (getPartOfSpeech() != null ? !getPartOfSpeech().equals(that.getPartOfSpeech()) : that.getPartOfSpeech() != null) {
        return false;
      }
      if (arguments.containsKey("definition") != that.arguments.containsKey("definition")) {
        return false;
      }
      if (getDefinition() != null ? !getDefinition().equals(that.getDefinition()) : that.getDefinition() != null) {
        return false;
      }
      if (arguments.containsKey("example") != that.arguments.containsKey("example")) {
        return false;
      }
      if (getExample() != null ? !getExample().equals(that.getExample()) : that.getExample() != null) {
        return false;
      }
      if (arguments.containsKey("synonyms") != that.arguments.containsKey("synonyms")) {
        return false;
      }
      if (getSynonyms() != null ? !getSynonyms().equals(that.getSynonyms()) : that.getSynonyms() != null) {
        return false;
      }
      if (arguments.containsKey("antonyms") != that.arguments.containsKey("antonyms")) {
        return false;
      }
      if (getAntonyms() != null ? !getAntonyms().equals(that.getAntonyms()) : that.getAntonyms() != null) {
        return false;
      }
      if (arguments.containsKey("folder") != that.arguments.containsKey("folder")) {
        return false;
      }
      if (getFolder() != null ? !getFolder().equals(that.getFolder()) : that.getFolder() != null) {
        return false;
      }
      if (arguments.containsKey("cardID") != that.arguments.containsKey("cardID")) {
        return false;
      }
      if (getCardID() != null ? !getCardID().equals(that.getCardID()) : that.getCardID() != null) {
        return false;
      }
      if (getActionId() != that.getActionId()) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + (getName() != null ? getName().hashCode() : 0);
      result = 31 * result + (getPartOfSpeech() != null ? getPartOfSpeech().hashCode() : 0);
      result = 31 * result + (getDefinition() != null ? getDefinition().hashCode() : 0);
      result = 31 * result + java.util.Arrays.hashCode(getExample());
      result = 31 * result + java.util.Arrays.hashCode(getSynonyms());
      result = 31 * result + java.util.Arrays.hashCode(getAntonyms());
      result = 31 * result + (getFolder() != null ? getFolder().hashCode() : 0);
      result = 31 * result + (getCardID() != null ? getCardID().hashCode() : 0);
      result = 31 * result + getActionId();
      return result;
    }

    @Override
    public String toString() {
      return "AddWordCard(actionId=" + getActionId() + "){"
          + "name=" + getName()
          + ", partOfSpeech=" + getPartOfSpeech()
          + ", definition=" + getDefinition()
          + ", example=" + getExample()
          + ", synonyms=" + getSynonyms()
          + ", antonyms=" + getAntonyms()
          + ", folder=" + getFolder()
          + ", cardID=" + getCardID()
          + "}";
    }
  }
}
