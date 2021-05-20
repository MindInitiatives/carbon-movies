package com.mindinitiatives.carbonmovies.model

import com.google.gson.annotations.SerializedName

class Movie(@field:SerializedName("poster_path") private var posterPath: String?, @field:SerializedName("adult") private var adult: Boolean, @field:SerializedName("overview") private var overview: String?, @field:SerializedName("release_date") private var releaseDate: String?, genreIds: List<Int?>?, id: Int?, originalTitle: String?, originalLanguage: String?, title: String?, backdropPath: String?, popularity: Double?, voteCount: Int?, video: Boolean?, voteAverage: Double?) {
    @SerializedName("genre_ids")
    private var genreIds: List<Int?>? = ArrayList()

    @SerializedName("id")
    private var id: Int?

    @SerializedName("original_title")
    private var originalTitle: String?

    @SerializedName("original_language")
    private var originalLanguage: String?

    @SerializedName("title")
    private var title: String?

    @SerializedName("backdrop_path")
    private var backdropPath: String?

    @SerializedName("popularity")
    private var popularity: Double?

    @SerializedName("vote_count")
    private var voteCount: Int?

    @SerializedName("video")
    private var video: Boolean?

    @SerializedName("vote_average")
    private var voteAverage: Double?

    fun getPosterPath(): String {
        return "https://image.tmdb.org/t/p/w500$posterPath"
    }

    fun setPosterPath(posterPath: String?) {
        this.posterPath = posterPath
    }

    fun isAdult(): Boolean {
        return adult
    }

    fun setAdult(adult: Boolean) {
        this.adult = adult
    }

    fun getOverview(): String? {
        return overview
    }

    fun setOverview(overview: String?) {
        this.overview = overview
    }

    fun getReleaseDate(): String? {
        return releaseDate
    }

    fun setReleaseDate(releaseDate: String?) {
        this.releaseDate = releaseDate
    }

    fun getGenreIds(): List<Int?>? {
        return genreIds
    }

    fun setGenreIds(genreIds: List<Int?>?) {
        this.genreIds = genreIds
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getOriginalTitle(): String? {
        return originalTitle
    }

    fun setOriginalTitle(originalTitle: String?) {
        this.originalTitle = originalTitle
    }

    fun getOriginalLanguage(): String? {
        return originalLanguage
    }

    fun setOriginalLanguage(originalLanguage: String?) {
        this.originalLanguage = originalLanguage
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getBackdropPath(): String? {
        return backdropPath
    }

    fun setBackdropPath(backdropPath: String?) {
        this.backdropPath = backdropPath
    }

    fun getPopularity(): Double? {
        return popularity
    }

    fun setPopularity(popularity: Double?) {
        this.popularity = popularity
    }

    fun getVoteCount(): Int? {
        return voteCount
    }

    fun setVoteCount(voteCount: Int?) {
        this.voteCount = voteCount
    }

    fun getVideo(): Boolean? {
        return video
    }

    fun setVideo(video: Boolean?) {
        this.video = video
    }

    fun getVoteAverage(): Double? {
        return voteAverage
    }

    fun setVoteAverage(voteAverage: Double?) {
        this.voteAverage = voteAverage
    }

    init {
        this.genreIds = genreIds
        this.id = id
        this.originalTitle = originalTitle
        this.originalLanguage = originalLanguage
        this.title = title
        this.backdropPath = backdropPath
        this.popularity = popularity
        this.voteCount = voteCount
        this.video = video
        this.voteAverage = voteAverage
    }
}