package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.DatabaseConnector
import com.nhlstenden.morithij.budgettracker.models.TagModel
import java.sql.Connection

class TagDAO  : DAO<TagModel>() {

    override fun get(id: Int): TagModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM tag WHERE id = $id")

        var tagRecord :  TagModel? = null

        if(resultSet.next()){
            tagRecord = TagModel(
                    resultSet.getInt("id"),
                    resultSet.getString("tag_name"),
                    resultSet.getString("hexcode")
            )
        }

        resultSet.close()
        statement.close()
        return tagRecord
    }

    override fun getAll(): List<TagModel> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM tag")

        val tags = mutableListOf<TagModel>()

        while(resultSet.next()){
            tags.add(
                TagModel(
                        resultSet.getInt("id"),
                        resultSet.getString("tag_name"),
                        resultSet.getString("hexcode")
                )
            )
        }

        resultSet.close()
        statement.close()
        return tags
    }

    override fun create(obj: TagModel): Int {
        val sql = "INSERT INTO tag (id, tag_name, hexcode) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.id)
        statement.setString(2, obj.tag_name)
        statement.setString(3, obj.hexcode)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        connection.close()

        return id
    }

    override fun update(obj: TagModel) {
        val statement = connection.prepareStatement("UPDATE tag SET tag_name = ?, hexcode = ? WHERE id = ?")
        statement.setString(1, obj.tag_name)
        statement.setString(2, obj.hexcode)
        statement.executeUpdate()
        statement.close()
    }

    fun createStandardTags() {
        System.out.println("meep")

        val standardTags = listOf(
                TagModel(1, "Car", "#4aaeff"),
                TagModel(2, "Groceries", "#33de61"),
                TagModel(3, "Entertainment", "#fff64a"),
                TagModel(4, "Utilities", "#f03cea" ),
                TagModel(5, "Health", "#dbfaff" ),
                TagModel(6, "Housing", "#e3d6ff" )
        )

        for (tag in standardTags) {
            create(tag)
        }
    }
}