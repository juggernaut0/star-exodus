package serialization

interface Serializer<TModel, TData> {
    fun save(model: TModel, refs: RefSaver): TData
    fun load(data: TData, refs: RefLoader): TModel
}
