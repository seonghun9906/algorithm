package com.example.demo;

import com.mxgraph.layout.*; // mxGraph 레이아웃 관련 패키지 import
import com.mxgraph.swing.mxGraphComponent; // mxGraph 컴포넌트 관련 패키지 import
import com.mxgraph.view.mxGraph; // mxGraph 관련 패키지 import

import javax.swing.*; // Swing GUI 관련 패키지 import
import java.util.*; // 자바 유틸리티 패키지 import

public class Astar {
    // Node 클래스 정의
    static class Node implements Comparable<Node> {
        String id; // 노드 식별자
        int cost; // 출발점으로부터의 비용
        List<String> path; // 노드까지의 경로

        // 생성자
        public Node(String id, int cost, List<String> path) {
            this.id = id;
            this.cost = cost;
            this.path = new ArrayList<>(path);
        }

        // Comparable 인터페이스 구현
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    // A* 알고리즘 구현
    public static List<String> aStarAlgorithm(Map<String, Map<String, Integer>> graph,
                                              Map<String, Integer> heuristics,
                                              String start, String end) {
        // 각 노드까지의 비용을 저장할 맵 초기화
        Map<String, Integer> distances = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE); // 무한대로 초기화
        }
        distances.put(start, 0); // 시작 노드의 비용은 0

        // 우선순위 큐를 이용하여 노드를 처리
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(start, heuristics.get(start), Collections.singletonList(start))); // 시작 노드 추가

        while (!queue.isEmpty()) {
            Node current = queue.poll(); // 우선순위 큐에서 비용이 가장 작은 노드 꺼냄

            if (current.id.equals(end)) { // 목적지에 도착한 경우
                return current.path; // 경로 반환
            }

            // 현재 노드의 이웃 노드들을 순회
            for (Map.Entry<String, Integer> adjacencyPair : graph.get(current.id).entrySet()) {
                String adjacent = adjacencyPair.getKey(); // 이웃 노드 식별자
                int weight = adjacencyPair.getValue(); // 현재 노드에서 이웃 노드까지의 비용

                int newCost = distances.get(current.id) + weight; // 새로운 비용 계산

                // 더 작은 비용으로 경로를 업데이트
                if (newCost < distances.get(adjacent)) {
                    distances.put(adjacent, newCost); // 비용 업데이트
                    List<String> newPath = new ArrayList<>(current.path);
                    newPath.add(adjacent); // 새로운 경로 추가
                    queue.add(new Node(adjacent, newCost + heuristics.get(adjacent) - heuristics.get(current.id), newPath)); // 큐에 추가
                }
            }
        }

        return null; // 경로를 찾을 수 없는 경우
    }

    public static void main(String[] args) {
        // 그래프 정의
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 1, "C", 3));
        graph.put("B", Map.of("D", 3));
        graph.put("C", Map.of("D", 1, "E", 6));
        graph.put("D", Map.of("F", 2));
        graph.put("E", Map.of("F", 2));
        graph.put("F", Collections.emptyMap());

        // 각 노드의 휴리스틱 값 정의
        Map<String, Integer> heuristics = Map.of("A", 9, "B", 7, "C", 8, "D", 5, "E", 3, "F", 0);

        // A* 알고리즘을 사용하여 최단 경로 찾기
        List<String> path = aStarAlgorithm(graph, heuristics, "A", "F");

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
        JFrame frame = new JFrame("A* Algorithm Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.pack();
        frame.setVisible(true);

        if (path != null) { // 경로가 있는 경우
            System.out.println("경로(출발점 A / 도착점 F): " + path); // 경로 출력
            int totalCost = 0;
            for (int i = 0; i < path.size() - 1; i++) { // 각 간선의 비용을 합산하여 경로의 총 비용 계산
                String current = path.get(i);
                String next = path.get(i + 1);
                totalCost += graph.get(current).get(next);
            }
            System.out.println("총 비용 : " + totalCost); // 총 비용 출력
        } else {
            System.out.println("No path found"); // 경로가 없는 경우
        }
    }
    }