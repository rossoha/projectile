package com.kyleu.projectile.models.graphql

import com.kyleu.projectile.graphql.{GraphQLContext, GraphQLSchema}
import sangria.execution.deferred.{DeferredResolver, Fetcher}
import com.kyleu.projectile.models.module.ApplicationFeatures.enabled
import sangria.schema._

abstract class BaseGraphQLSchema extends GraphQLSchema {
  protected[this] def additionalFetchers = List.empty[Fetcher[GraphQLContext, _, _, _]]
  protected[this] def additionalQueryFields = List.empty[Field[GraphQLContext, Unit]]
  protected[this] def additionalMutationFields = List.empty[Field[GraphQLContext, Unit]]

  private[this] lazy val baseFetchers: List[Fetcher[GraphQLContext, _, _, _]] = List(
    if (enabled("user")) { Seq(com.kyleu.projectile.models.graphql.user.SystemUserSchema.systemUserByPrimaryKeyFetcher) } else Nil,
    if (enabled("audit")) { Seq(com.kyleu.projectile.models.graphql.audit.AuditRecordSchema.auditRecordByAuditIdFetcher) } else Nil,
    if (enabled("audit")) { Seq(com.kyleu.projectile.models.graphql.audit.AuditRecordSchema.auditRecordByPrimaryKeyFetcher) } else Nil,
    if (enabled("audit")) { Seq(com.kyleu.projectile.models.graphql.audit.AuditSchema.auditByPrimaryKeyFetcher) } else Nil,
    if (enabled("note")) { Seq(com.kyleu.projectile.models.graphql.note.NoteSchema.noteByAuthorFetcher) } else Nil,
    if (enabled("note")) { Seq(com.kyleu.projectile.models.graphql.note.NoteSchema.noteByPrimaryKeyFetcher) } else Nil,
    if (enabled("task")) { Seq(com.kyleu.projectile.models.graphql.task.ScheduledTaskRunSchema.scheduledTaskRunByPrimaryKeyFetcher) } else Nil
  ).flatten

  override final lazy val resolver = DeferredResolver.fetchers(additionalFetchers ++ baseFetchers: _*)

  // Query Types
  private[this] lazy val baseQueryFields: List[Field[GraphQLContext, Unit]] = List(
    if (enabled("user")) { com.kyleu.projectile.models.graphql.user.SystemUserSchema.queryFields } else Nil,
    if (enabled("audit")) { com.kyleu.projectile.models.graphql.audit.AuditRecordSchema.queryFields } else Nil,
    if (enabled("audit")) { com.kyleu.projectile.models.graphql.audit.AuditSchema.queryFields } else Nil,
    if (enabled("note")) { com.kyleu.projectile.models.graphql.note.NoteSchema.queryFields } else Nil,
    if (enabled("task")) { com.kyleu.projectile.models.graphql.task.ScheduledTaskRunSchema.queryFields } else Nil,
    if (enabled("sandbox")) { com.kyleu.projectile.models.graphql.sandbox.SandboxSchema.queryFields } else Nil
  ).flatten

  override final lazy val queryType = ObjectType(name = "Query", description = "The main query interface.", fields = additionalQueryFields ++ baseQueryFields)

  // Mutation Types
  private[this] lazy val baseMutationFields: List[Field[GraphQLContext, Unit]] = List(
    if (enabled("user")) { com.kyleu.projectile.models.graphql.user.SystemUserSchema.mutationFields } else Nil,
    if (enabled("audit")) { com.kyleu.projectile.models.graphql.audit.AuditRecordSchema.mutationFields } else Nil,
    if (enabled("audit")) { com.kyleu.projectile.models.graphql.audit.AuditSchema.mutationFields } else Nil,
    if (enabled("note")) { com.kyleu.projectile.models.graphql.note.NoteSchema.mutationFields } else Nil,
    if (enabled("task")) { com.kyleu.projectile.models.graphql.task.ScheduledTaskRunSchema.mutationFields } else Nil,
    if (enabled("sandbox")) { com.kyleu.projectile.models.graphql.sandbox.SandboxSchema.mutationFields } else Nil
  ).flatten

  override final lazy val mutationType = ObjectType("Mutation", "The main mutation interface.", fields = additionalMutationFields ++ baseMutationFields)
}
