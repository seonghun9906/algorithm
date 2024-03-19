package com.example.demo;

import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.*;

public class BellmanFordVisualization {

    public static void main(String[] args) {
        // 그래프 생성
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 4, "C", 2));
        graph.put("B", Map.of("C", 5, "D", 10));
        graph.put("C", Map.of("D", 3, "E", 2));
        graph.put("D", Map.of("E", 4));
        graph.put("E", Collections.emptyMap());

        // 시작점으로부터 각 노드까지의 최단 거리와 경로 저장
        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            paths.put(node, new ArrayList<>());
        }
        distances.put("A", 0);
        paths.get("A").add("A");

        // 벨만-포드 알고리즘을 사용하여 최단 경로 계산
        for (int i = 0; i < graph.size() - 1; i++) {
            for (String source : graph.keySet()) {
                for (Map.Entry<String, Integer> adjacencyPair : graph.get(source).entrySet()) {
                    String target = adjacencyPair.getKey();
                    int weight = adjacencyPair.getValue();
                    if (distances.get(source) != Integer.MAX_VALUE && distances.get(source) + weight < distances.get(target)) {
                        distances.put(target, distances.get(source) + weight);
                        List<String> newPath = new ArrayList<>(paths.get(source));
                        newPath.add(target);
                        paths.put(target, newPath);
                    }
                }
            }
        }

        // 그래프 시각화
        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();
        mxGraph.getModel().beginUpdate();
        try {
            Object[] nodes = new Object[graph.size()];
            int x = 50, y = 50;
            for (String node : graph.keySet()) {
                nodes[node.charAt(0) - 'A'] = mxGraph.insertVertex(parent, null, node, x, y, 30, 30);
                x += 100;
            }
            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                String source = entry.getKey();
                for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                    String target = edge.getKey();
                    mxGraph.insertEdge(parent, null, edge.getValue().toString(), nodes[source.charAt(0) - 'A'], nodes[target.charAt(0) - 'A']);
                }
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        // 그래프 레이아웃 설정
        new mxCompactTreeLayout(mxGraph).execute(parent);

        // 그래프 컴포넌트 생성
        mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);

        // Swing 프레임 생성 및 그래프 컴포넌트 추가
        JFrame frame = new JFrame("Bellman-Ford Algorithm Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.pack();
        frame.setVisible(true);

        // 콘솔에 결과 출력
        System.out.println("출발점 A로부터 각 노드까지의 최단 거리: " + distances);
        System.out.println("각 노드별 경로: " + paths);
    }
}