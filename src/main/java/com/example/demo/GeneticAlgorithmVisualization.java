package com.example.demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmVisualization extends JFrame {

    private DefaultXYDataset dataset;
    private JFreeChart chart;

    private static final int CHROMOSOME_LENGTH = 10;
    private static final int POPULATION_SIZE = 10;
    private static final double MUTATION_RATE = 0.01;

    public GeneticAlgorithmVisualization(String title) {
        super(title);
        dataset = new DefaultXYDataset();
        chart = ChartFactory.createXYLineChart(
                "세대별 적합도 변화", // chart title (차트 제목)
                "세대",               // x axis label (x축 라벨)
                "최고 적합도",             // y axis label (y축 라벨)
                dataset
        );

        // 폰트 설정
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 18);
        Font axisLabelFont = new Font("맑은 고딕", Font.PLAIN, 14);
        Font tickLabelFont = new Font("맑은 고딕", Font.PLAIN, 12);

        chart.getTitle().setFont(titleFont);
        chart.getXYPlot().getDomainAxis().setLabelFont(axisLabelFont);
        chart.getXYPlot().getRangeAxis().setLabelFont(axisLabelFont);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(tickLabelFont);
        chart.getXYPlot().getRangeAxis().setTickLabelFont(tickLabelFont);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    // 개체를 생성하는 메서드
    private static String createIndividual() {
        StringBuilder individual = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            individual.append(random.nextInt(2));
        }
        return individual.toString();
    }

    // 개체 집단을 초기화하는 메서드
    private static List<String> initializePopulation() {
        List<String> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(createIndividual());
        }
        return population;
    }

    // 적합도를 계산하는 메서드 (이진 문자열을 십진수로 변환)
    private static double calculateFitness(String individual) {
        return Integer.parseInt(individual, 2);
    }

    // 돌연변이를 수행하는 메서드
    private static String mutate(String individual) {
        StringBuilder mutatedIndividual = new StringBuilder(individual);
        Random random = new Random();
        int mutationPoint = random.nextInt(CHROMOSOME_LENGTH);
        mutatedIndividual.setCharAt(mutationPoint, (char) ('0' + ('1' - individual.charAt(mutationPoint))));
        return mutatedIndividual.toString();
    }

    // 유전 알고리즘 실행
    public void runGeneticAlgorithm() {
        List<String> population = initializePopulation();
        int generation = 1;
        double[][] seriesData = new double[2][0];
        while (true) {
            // 개체 평가
            double maxFitness = Double.MIN_VALUE;
            String fittestIndividual = "";
            for (String individual : population) {
                double fitness = calculateFitness(individual);
                if (fitness > maxFitness) {
                    maxFitness = fitness;
                    fittestIndividual = individual;
                }
            }
            System.out.println("세대 " + generation + ": 최적 해 " + fittestIndividual + ", 최적 적합도 " + maxFitness);

            // 그래프 데이터 추가
            double[][] newData = new double[2][seriesData[0].length + 1];
            for (int i = 0; i < seriesData[0].length; i++) {
                newData[0][i] = seriesData[0][i];
                newData[1][i] = seriesData[1][i];
            }
            newData[0][seriesData[0].length] = generation;
            newData[1][seriesData[0].length] = maxFitness;
            seriesData = newData;

            dataset.addSeries("최고 적합도", seriesData);

            // 종료 조건 (최적 해 발견 시 종료)
            if (maxFitness == Math.pow(2, CHROMOSOME_LENGTH) - 1) {
                break;
            }

            // 새로운 개체 생성
            List<String> newPopulation = new ArrayList<>();
            for (String individual : population) {
                newPopulation.add(mutate(individual));
            }
            population = newPopulation;
            generation++;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GeneticAlgorithmVisualization frame = new GeneticAlgorithmVisualization("유전 알고리즘 시각화");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.runGeneticAlgorithm();
        });
    }
}
