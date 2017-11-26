package serialization

interface Serializer<TModel : Serializable, TData> {
    fun save(model: TModel): TData
    fun load(data: TData): TModel
}
