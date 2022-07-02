package me.nekocloud.base.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class CollectionUtil {

    /**
     * Проверить наличие элементов одной коллекции в другой
     *
     * @param mainCollection - главная коллекция, в которой будем находить элементы
     * @param targetCollection - целевая коллекция, из нее будет получать необходимые элементы
     */
    public <E> boolean containsElements(@NotNull Collection<E> mainCollection,
                                        @NotNull Collection<E> targetCollection) {

        for (E collectionElement : targetCollection) {
            if (mainCollection.contains(collectionElement)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Проверить наличие всех элементов одной коллекции в другой
     *
     * @param mainCollection - главная коллекция, в которой будем находить элеенты
     * @param targetCollection - целевая коллекция, из нее будет получать необходимые элементы
     */
    public <E> boolean containsAllElements(@NotNull Collection<E> mainCollection,
                                           @NotNull Collection<E> targetCollection) {
        int containsCounter = 0;

        for (E collectionElement : targetCollection) {
            if (mainCollection.contains(collectionElement)) {
                containsCounter++;
            }
        }

        return containsCounter >= targetCollection.size();
    }

    /**
     * Изменить строки коллекции, сделав
     * их все маленькими буквами
     *
     * @param collection - коллекция
     */
    public <T extends Collection<String>> T toLowerCase(@NotNull T collection) {
        return handleCollection(collection, String::toLowerCase);
    }

    /**
     * Изменить строки коллекции, сделав
     * их все большими буквами
     *
     * @param collection - коллекция
     */
    public <T extends Collection<String>> T toUpperCase(@NotNull T collection) {
        return handleCollection(collection, String::toUpperCase);
    }

    /**
     * Обработать элементы коллеции
     *
     * @param collection - коллекция
     * @param collectionHandler - обработчик коллекции
     */
    public <E, T extends Collection<E>> T handleCollection(@NotNull T collection, @NotNull CollectionHandler<E> collectionHandler) {
        List<E> collectionList = ((List<E>) collection);

        for (int index = 0 ; index < collection.size(); index++) {
            E collectionElement = collectionList.get(index);

            collectionList.set(index, collectionHandler.handleElement(collectionElement));
        }

        return collection;
    }

    /**
     * Перестроить коллекию в новую
     *
     * @param collectionFrom - коллеция, которую перерабатываем
     * @param newCollectionGeneric - новый тип дженерика коллекции
     * @param collectionRebuilder - обработчик переработки коллекции
     */
    public <F, T, C extends Collection<T>> C rebuildCollection(
            @NotNull Collection<? extends F> collectionFrom,
            @NotNull Class<T> newCollectionGeneric,
            @NotNull CollectionRebuilder<T, F> collectionRebuilder
    ) {

        C resultCollection = (C) new ArrayList<T>();

        for (F elementFrom : collectionFrom) {
            resultCollection.add(collectionRebuilder.rebuildElement(elementFrom));
        }

        return resultCollection;
    }

    /**
     * Создать общую коллекцию из всех элементов
     * перечисленный коллекций в аргументе
     *
     * @param collections - коллекции для объединения
     */
    public <T, C extends Collection<T>> C createGlobalCollection(@NotNull C... collections) {
        Collection<T> globalCollection = new ArrayList<T>();

        for (C collection : collections) {
            globalCollection.addAll(collection);
        }

        return (C) globalCollection;
    }

    /**
     * Отфильтровать текущую коллекцию
     *
     * @param collection - коллеция
     * @param filterPredicate - фильтр элементов
     */
    public <T, C extends Collection<T>> C filterCollection(@NotNull C collection,
                                                           @NotNull Predicate<T> filterPredicate) {
        return (C) collection.stream()
                .filter(filterPredicate).collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Обработчик изменения элементов коллекции
     *
     * @param <E> - тип элемента коллекции
     */
    public interface CollectionHandler<E> {

        /**
         * Обработать элемент коллекции
         *
         * @param element - элемент
         */
        E handleElement(E element);
    }

    /**
     * Обработчик переработки элементов коллекции
     *
     * @param <T> - тип элемента коллекции, которую возвращают
     * @param <F> - тип элемента коллекции, которую перерабатывают
     */
    public interface CollectionRebuilder<T, F> {

        /**
         * Обработать элемент коллекции
         *
         * @param element - элемент
         */
        T rebuildElement(F element);
    }

    public <K, V> Map<K, V> shuffle(Map<K, V> map) {
        List<K> keys = new ArrayList<>(map.keySet());

        Collections.shuffle(keys);

        Map<K, V> shuffledMap = new LinkedHashMap<>();
        keys.forEach(key -> shuffledMap.put(key, map.get(key)));

        return shuffledMap;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
