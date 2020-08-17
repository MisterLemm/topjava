package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 9, 0), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 10, 0), "Перекус", 250),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 11, 0), "Обед", 800),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 22, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 8, 30), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 11, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 20, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

//        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(13, 0), 2000);
//        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> filteredListMealExcess = new ArrayList<>();
        meals.sort(Comparator.comparing(UserMeal::getDateTime));

        Map<LocalDate, Integer> mapKey = new HashMap<>();

        for (UserMeal userMeal : meals) {
            mapKey.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }

        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(meal.getDateTime()), startTime, endTime)) {
                Integer calories = mapKey.get(meal.getDateTime().toLocalDate());
                filteredListMealExcess.add(new UserMealWithExcess(meal.getDateTime(),
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesPerDay >= calories));
            }
        }

        return filteredListMealExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> mapKey = meals.stream()
                .collect(Collectors.groupingBy(l -> l.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .sorted(Comparator.comparing(UserMeal::getDateTime))
                .map(getFunctionStream(caloriesPerDay, mapKey))
                .collect(Collectors.toList());
    }

    private static Function<UserMeal, UserMealWithExcess> getFunctionStream(int caloriesPerDay, Map<LocalDate, Integer> mapKey) {
        return (UserMeal meal) -> {
            int caloriesMeal = mapKey.get(meal.getDateTime().toLocalDate());
            return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), caloriesPerDay >= caloriesMeal);
        };
    }

}
