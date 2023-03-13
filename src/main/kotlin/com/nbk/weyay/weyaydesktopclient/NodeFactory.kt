package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class NodeFactory<T, R>(
    private val nodes: ObservableList<R>,
    private val data: ObservableList<T>,
    private val buildNodeFor: (model: T) -> R,
    private val additionalNodes: Collection<R> = emptyList()
) {
    init {
        data.addListener(ListChangeListener { ch ->
            while (ch.next()) {
                if (ch.wasPermutated()) {
                    val permutedNodes = mutableListOf<R>()
                    for (oldIndex in (ch.from until ch.to)) {
                        val newIndex = ch.getPermutation(oldIndex)
                        permutedNodes.add(newIndex - ch.from, nodes[oldIndex])
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
                    if (nodes.size > 0) nodes.remove(ch.from, ch.from + ch.removedSize)
                    if (nodes.size == 1) nodes.clear()
                } else if (ch.wasAdded()) {
                    val initialSize = nodes.size
                    nodes.addAll(ch.from, buildNodesFor(ch.addedSubList))
                    if (initialSize == 0) {
                        nodes.addAll(additionalNodes)
                    }
                }
            }
        })
        rebuildAllNodes()

    }

    private fun buildNodesFor(dataIn: List<T>): List<R> {
        return dataIn.fold(mutableListOf()) { list, d ->
            list.add(buildNodeFor(d))
            list
        }
    }

    private fun rebuildAllNodes() {
        nodes.setAll(buildNodesFor(data))
    }
}