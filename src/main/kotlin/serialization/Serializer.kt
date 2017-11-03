package serialization

interface Serializer<TModel, TData> {
    fun serialize(obj: TModel): TData
    fun deserialize(serModel: TData): TModel
}
