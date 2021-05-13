package com.tunepruner.fingerperc.launchscreen.soundpackDetail

import com.tunepruner.fingerperc.launchscreen.librarylist.BillingClientListener
import com.tunepruner.fingerperc.launchscreen.librarylist.BillingClientWrapper


import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.android.billingclient.api.*
import com.tunepruner.fingerperc.launchscreen.librarylist.LibraryDetails

class SoundpackRepository(val app: Application, val filter: String) : BillingClientListener {
    val libraryListLiveData = MutableLiveData<List<LibraryDetails>>()
    var libraryListPrimitive = ArrayList<LibraryDetails>()
    val db = Firebase.firestore
    private val TAG = "SnpkRepo.Class"
    private lateinit var billingClientWrapper: BillingClientWrapper



    init {
        getData()
    }

    private fun getData() {
        getLibraryNamesFromFirebase()
    }

    private fun getLibraryNamesFromFirebase() {
        val collectionRef = db.collection("libraries")
        val libraryDetails: ArrayList<LibraryDetails> = ArrayList()

        collectionRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    val results: List<LibraryDetails> = it.toObjects(LibraryDetails::class.java)
                    for (element in results) {
                        libraryDetails.add(element)
                    }

//                    val libraryDetailsListSorted = sortByStatus(libraryDetails)
                    val libraryDetailsThisSoundpack = filterToThisSoundpack(libraryDetails)
                    val libraryDetailsListWithInstalledStatus =
                        updateInstallStatuses(libraryDetailsThisSoundpack)
                    libraryListPrimitive = libraryDetailsListWithInstalledStatus
                    billingClientWrapper =
                        BillingClientWrapper.getInstance(this, app.applicationContext)

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun filterToThisSoundpack(libraryDetails: ArrayList<LibraryDetails>): ArrayList<LibraryDetails> {
        val listThisSoundpack = ArrayList<LibraryDetails>()
        for (element in libraryDetails) {
            if (element.soundpackID == filter) {
                listThisSoundpack.add(element)
            }
        }
        return listThisSoundpack
    }

    override fun onClientReady() {
        billingClientWrapper.queryPurchases()
    }

    override fun onPurchasesQueried(soundpacksPurchased: ArrayList<Purchase>) {
        updatePurchaseStatuses(soundpacksPurchased)
        libraryListLiveData.value = libraryListPrimitive
    }

    private fun updateInstallStatuses(libraryDetails: ArrayList<LibraryDetails>): ArrayList<LibraryDetails> {
        val assetManager: AssetManager = app.assets
        val filePaths = assetManager.list("audio")
            ?: error("AssetManager couldn't get filePaths")
        for (i in 0..(libraryDetails.lastIndex)) {
            for (j in 0..filePaths.lastIndex) {
                val libraryName = libraryDetails[i].libraryID.toString()
                Log.i(TAG, filePaths[j])
                if (filePaths[j].contains(libraryName)) {
                    libraryDetails[i].isInstalled = true
                    Log.i(TAG, "${libraryDetails[i].libraryName} is installed!")
                    break
                }
            }
            if (libraryDetails[i].isInstalled == null) {
                libraryDetails[i].isInstalled = false
                Log.i(TAG, "${libraryDetails[i].libraryName} is not installed...")

            }
        }
        return libraryDetails
    }

    private fun updatePurchaseStatuses(listOfPurchases: ArrayList<Purchase>) {
        var counter1 = 1
        for (libraryDetails in libraryListPrimitive) {
//            Log.i(TAG, "libraryListPrimitive item # ${counter1++} = ${libraryDetails}: ")
            if (libraryDetails.isPurchased == null &&
                libraryDetails.soundpackID != null
            ) {
                var counter = 1
                for (purchase in listOfPurchases) {
//                    Log.i(TAG, "listOfPurchases item # ${counter++} = ${purchase.sku}: ")
                    if (purchase.sku == libraryDetails.soundpackID) {
//                        Log.i(TAG, "starting library details: ")
                        libraryDetails.isPurchased = true
//                        Log.i(TAG, "changing ${purchase.sku} to true: ")
                    }else{
//                        Log.i(TAG, "${purchase.sku} doesn't equal ${libraryDetails.soundpackID}: ")
                    }
                }
            }
        }
    }
}

private fun sortByStatus(libraryDetails: ArrayList<LibraryDetails>): ArrayList<LibraryDetails> {
//        val unsorted = ArrayList<LibraryDetails>()
    val sorted = ArrayList<LibraryDetails>()
//        for (element in libraryDetails) {
//            unsorted.add(element)
//        }
    for (element in libraryDetails) {
        sorted.add(element)
    }
    sorted.sortWith(compareByDescending { it.isPurchased })
    return sorted
}