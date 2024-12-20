package com.example.foodpro

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class Recipe(
    val title: String = "",
    val author: String = "",
    val `ingredients-count`: String = "",
    val time: String = "",
    val category: String = "",
    val cuisine: String = "",
    val menu: String = "",
    val image: String = "",
    val likes: String = "",
    val dislikes: String = "",
    val bookmarks: String = "",
    val description: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbohydrates: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val recipeInstruction: List<RecipeInstruction> = emptyList(),
    val id: String = ""
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Ingredient.CREATOR) ?: emptyList(),
        parcel.createTypedArrayList(RecipeInstruction.CREATOR) ?: emptyList(),
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(`ingredients-count`)
        parcel.writeString(time)
        parcel.writeString(category)
        parcel.writeString(cuisine)
        parcel.writeString(menu)
        parcel.writeString(image)
        parcel.writeString(likes)
        parcel.writeString(dislikes)
        parcel.writeString(bookmarks)
        parcel.writeString(description)
        parcel.writeString(calories)
        parcel.writeString(proteins)
        parcel.writeString(fats)
        parcel.writeString(carbohydrates)
        parcel.writeTypedList(ingredients)
        parcel.writeTypedList(recipeInstruction)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}



data class Ingredient(
    val item: String = "",
    val quantity: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(item)
        parcel.writeString(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }
}

data class RecipeInstruction(
    val description: String = "",
    val stepImage: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(stepImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeInstruction> {
        override fun createFromParcel(parcel: Parcel): RecipeInstruction {
            return RecipeInstruction(parcel)
        }

        override fun newArray(size: Int): Array<RecipeInstruction?> {
            return arrayOfNulls(size)
        }
    }
}


