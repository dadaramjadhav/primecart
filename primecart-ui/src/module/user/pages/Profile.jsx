import { useEffect, useState } from "react"
import { useForm } from "react-hook-form"

import useCustomer from "../hooks/useCustomer"
import useUpdateCustomer from "../hooks/useUpdateCustomer"
import { showError, showSuccess } from "../../../shared/utils/notifications"

export default function Profile() {
  const [isEditing, setIsEditing] = useState(false)

  const { customer, isLoading, isError, error, isFetching, refetch } = useCustomer()

  const { updateCustomer, isUpdating } = useUpdateCustomer()

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm()

  useEffect(() => {
    if (!customer) {
      return
    }

    reset({
      id: customer.id ?? "",
      keycloakUserId: customer.keycloakUserId ?? "",
      username: customer.username ?? "",
      firstName: customer.firstName ?? "",
      lastName: customer.lastName ?? "",
      email: customer.email ?? "",
      phone: customer.phone ?? "",
    })
  }, [customer, reset])

  function startEditing() {
    setIsEditing(true)
  }

  function cancelEditing() {
    reset({
      id: customer.id ?? "",
      keycloakUserId: customer.keycloakUserId ?? "",
      username: customer.username ?? "",
      firstName: customer.firstName ?? "",
      lastName: customer.lastName ?? "",
      email: customer.email ?? "",
      phone: customer.phone ?? "",
    })

    setIsEditing(false)
  }

  async function onSubmit(formData) {
    // Whitelist only fields that the user may edit.
    const profileData = {
      firstName: formData.firstName.trim(),
      lastName: formData.lastName.trim(),
      email: formData.email.trim(),
      phone: formData.phone.trim(),
    }

    try {
      await updateCustomer(profileData)

      showSuccess("Profile updated successfully.")
      setIsEditing(false)
    } catch (updateError) {
      showError(updateError, "Unable to update your profile.")
    }
  }

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading profile...</div>
  }

  if (isError) {
    return <div className="p-8 text-center text-destructive">{error?.message ?? "Unable to load profile."}</div>
  }

  if (!customer) {
    return <div className="p-8 text-center text-muted-foreground">Profile not found.</div>
  }

  return (
    <div className="mx-auto max-w-2xl py-8">
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="rounded-xl border bg-card p-6 text-card-foreground shadow-sm"
        noValidate>
        <div className="flex items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold">My Profile</h1>

            {isFetching && <p className="text-sm text-muted-foreground">Refreshing profile...</p>}
          </div>

          {!isEditing && (
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => refetch()}
                disabled={isFetching}
                className="rounded-md border px-4 py-2 hover:bg-accent disabled:opacity-50">
                Refresh
              </button>

              <button
                type="button"
                onClick={startEditing}
                className="rounded-md bg-primary px-4 py-2 text-primary-foreground hover:bg-primary/90">
                Edit Profile
              </button>
            </div>
          )}
        </div>

        <div className="mt-6 grid gap-5 sm:grid-cols-2">
          <ProfileField label="User ID" readOnly register={register("id")} />

          <ProfileField label="Keycloak User ID" readOnly register={register("keycloakUserId")} />

          <ProfileField label="Username" readOnly register={register("username")} />

          <ProfileField
            label="First Name"
            readOnly={!isEditing}
            register={register("firstName", {
              required: "First name is required.",
              maxLength: {
                value: 50,
                message: "First name cannot exceed 50 characters.",
              },
            })}
            error={errors.firstName?.message}
          />

          <ProfileField
            label="Last Name"
            readOnly={!isEditing}
            register={register("lastName", {
              required: "Last name is required.",
              maxLength: {
                value: 50,
                message: "Last name cannot exceed 50 characters.",
              },
            })}
            error={errors.lastName?.message}
          />

          <ProfileField
            label="Email"
            type="email"
            readOnly={!isEditing}
            register={register("email", {
              required: "Email is required.",
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: "Enter a valid email address.",
              },
            })}
            error={errors.email?.message}
          />

          <ProfileField
            label="Phone"
            type="tel"
            readOnly={!isEditing}
            register={register("phone", {
              required: "Phone number is required.",
              pattern: {
                value: /^[0-9+\-\s()]{7,20}$/,
                message: "Enter a valid phone number.",
              },
            })}
            error={errors.phone?.message}
          />
        </div>

        {isEditing && (
          <div className="mt-8 flex justify-end gap-3">
            <button
              type="button"
              onClick={cancelEditing}
              disabled={isUpdating}
              className="rounded-md border px-5 py-2 hover:bg-accent disabled:opacity-50">
              Cancel
            </button>

            <button
              type="submit"
              disabled={isUpdating}
              className="rounded-md bg-primary px-5 py-2 text-primary-foreground hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50">
              {isUpdating ? "Saving..." : "Save Changes"}
            </button>
          </div>
        )}
      </form>
    </div>
  )
}

function ProfileField({ label, type = "text", readOnly, register, error }) {
  return (
    <div>
      <label className="mb-1.5 block text-sm font-medium">{label}</label>

      <input
        type={type}
        readOnly={readOnly}
        className={`w-full rounded-md border px-3 py-2 outline-none transition ${
          readOnly
            ? "cursor-default bg-muted text-muted-foreground"
            : "bg-background focus:border-primary focus:ring-2 focus:ring-primary/20"
        }`}
        {...register}
      />

      {error && <p className="mt-1 text-sm text-destructive">{error}</p>}
    </div>
  )
}
