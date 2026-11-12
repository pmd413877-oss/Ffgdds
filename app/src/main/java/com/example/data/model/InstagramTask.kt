package com.example.data.model

data class InstagramTask(
    val id: String,
    val targetUsername: String,
    val profileUrl: String = "https://www.instagram.com/$targetUsername/",
    val title: String,
    val rewardPoints: Int = 50,
    val status: TaskStatus = TaskStatus.PENDING,
    val followersCount: String = "10K+",
    val instructions: String = "ইনস্টাগ্রাম প্রোফাইলে গিয়ে Follow বাটনে ক্লিক করুন। অটো স্ক্রিনশট হয়ে সার্ভারে সাবমিট হবে।"
)

enum class TaskStatus {
    PENDING,       // অপেক্ষমাণ
    IN_PROGRESS,   // চলমান
    SUBMITTED,     // সার্ভারে জমা হয়েছে
    VERIFIED       // যাচাই সম্পন্ন
}
