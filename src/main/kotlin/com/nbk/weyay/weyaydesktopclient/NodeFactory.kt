package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node

class NodeFactory<T>(
    private val nodes: ObservableList<Node>,
    private val data: ObservableList<T>,
    private val buildNodeFor: (model: T) -> Node
) {
    init {
        data.addListener(ListChangeListener { ch ->
            while (ch.next()) {
                if (ch.wasPermutated()) {
                    val permutedNodes = mutableListOf<Node>()
                    for (oldIndex in (ch.from until ch.to)) {
                        val newIndex = ch.getPermutation(oldIndex)
                        permutedNodes.add(newIndex - ch.from, nodes.get(oldIndex))
                    }
                    nodes.remove(ch.from, ch.to)
                    nodes.addAll(ch.from, permutedNodes)
                } else if (ch.wasUpdated()) {
                    for (index in (ch.from until ch.to)) {
                        val model = data[index]
                        val node = buildNodeFor(model)
                        nodes[index] = node
                    }
                } else if (ch.wasRemoved()) {
                    nodes.remove(ch.from, ch.from + ch.removedSize)
                } else if (ch.wasAdded()) {
                    nodes.addAll(ch.from, buildNodesFor(ch.addedSubList))
                }
            }
        })
        rebuildAllNodes()
    }

    private fun buildNodesFor(dataIn: List<T>): List<Node> {
        return dataIn.fold(mutableListOf()) { list, d ->
            list.add(buildNodeFor(d))
            list
        }
    }

    private fun rebuildAllNodes() {
        nodes.setAll(buildNodesFor(data))
    }
}