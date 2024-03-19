package com.example.demo;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.*;

public class Dijkstra {
    // Node 클래스 정의
    static class Node implements Comparable<Node> {
        String id; // 노드 식별자
        int distance; // 출발점으로부터의 거리

        // 생성자
        public Node(String id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        // Comparable 인터페이스 구현
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    // 다익스트라 알고리즘 구현
    public static AbstractMap.SimpleEntry<Map<String, Integer>, Map<String, List<String>>> dijkstra(
            Map<String, Map<String, Integer>> graph, String start) {

        // 각 노드까지의 최단 거리와 최단 경로를 저장할 맵들 초기화
        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE); // 최단 거리를 무한대로 초기화
            paths.put(node, new ArrayList<>()); // 최단 경로를 빈 리스트로 초기화
        }
        distances.put(start, 0); // 시작 노드의 최단 거리는 0
        paths.get(start).add(start); // 시작 노드의 최단 경로는 자기 자신

        // 우선순위 큐를 이용한 다익스트라 알고리즘 수행
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(start, 0)); // 시작 노드를 큐에 추가

        while (!queue.isEmpty()) {
            Node current = queue.poll(); // 우선순위 큐에서 가장 짧은 거리의 노드를 꺼냄

            if (current.distance > distances.get(current.id)) continue; // 불필요한 연산을 건너뜀

            // 현재 노드에 인접한 모든 노드들을 순회
            for (Map.Entry<String, Integer> adjacencyPair : graph.get(current.id).entrySet()) {
                String adjacent = adjacencyPair.getKey(); // 인접한 노드 식별자
                int weight = adjacencyPair.getValue(); // 현재 노드에서 인접한 노드까지의 거리

                int distance = current.distance + weight; // 출발점에서 인접한 노드까지의 거리 계산

                // 더 짧은 거리로 최단 거리 갱신
                if (distance < distances.get(adjacent)) {
                    distances.put(adjacent, distance);
                    queue.add(new Node(adjacent, distance)); // 큐에 최단 거리의 노드 추가

                    // 최단 경로 갱신
                    List<String> newPath = new ArrayList<>(paths.get(current.id));
                    newPath.add(adjacent);
                    paths.put(adjacent, newPath);
                }
            }
        }

        // 결과 반환
        return new AbstractMap.SimpleEntry<>(distances, paths);
    }

    public static void main(String[] args) {
        // 그래프 정의
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 4, "C", 2));
        graph.put("B", Map.of("C", 5, "D", 10));
        graph.put("C", Map.of("D", 3, "E", 2));
        graph.put("D", Map.of("E", 4));
        graph.put("E", Collections.emptyMap());

        // 다익스트라 알고리즘 수행 결과 받기
        AbstractMap.SimpleEntry<Map<String, Integer>, Map<String, List<String>>> result = dijkstra(graph, "A");
        Map<String, Integer> distances = result.getKey(); // 각 노드까지의 최단 거리
        Map<String, List<String>> paths = result.getValue(); // 각 노드까지의 최단 경로

        // 콘솔에 결과 출력
        System.out.println("거리 (출발점 A): " + distances);
        System.out.println("각 노드별 경로: " + paths);

        // mxGraph를 사용하여 그래프 시각화
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
        JFrame frame = new JFrame("Dijkstra Algorithm Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.pack();
        frame.setVisible(true);
    }
}