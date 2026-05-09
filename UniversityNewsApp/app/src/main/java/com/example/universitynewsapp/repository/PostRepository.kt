package com.example.universitynewsapp.repository

import com.example.universitynewsapp.model.Comment
import com.example.universitynewsapp.model.Post
import com.example.universitynewsapp.model.User
import com.example.universitynewsapp.network.RetrofitClient
import java.io.IOException
import retrofit2.HttpException

class PostRepository {
    private val api = RetrofitClient.instance

    suspend fun getAllPosts(): Result<List<Post>> {
        return try {
            val posts = api.getAllPosts()
            Result.success(posts)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostById(id: Int): Result<Post> {
        return try {
            val post = api.getPostById(id)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommentsByPost(postId: Int): Result<List<Comment>> {
        return try {
            val comments = api.getCommentsByPost(postId)
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val users = api.getAllUsers()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Int): Result<User> {
        return try {
            val user = api.getUserById(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostsByUser(userId: Int): Result<List<Post>> {
        return try {
            val posts = api.getPostsByUser(userId)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

