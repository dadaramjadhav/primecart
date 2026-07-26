import { useEffect, useState } from "react"
import { useForm } from "react-hook-form"

import { showError, showSuccess } from "../../../shared/utils/notifications"
import useCreateCustomer from "../hooks/useCreateCustomer"
import useCustomer from "../hooks/useCustomer"
import useUpdateCustomer from "../hooks/useUpdateCustomer"

export default function Profile() {
  const [isEditing, setIsEditing] = useState(false)

  const { customer, isLoading, isError, error, isFetching, refetch } = useCustomer()
  const { createCustomer, isCreating } = useCreateCustomer()
  const { updateCustomer, isUpdating } = useUpdateCustomer()
  const isSaving = isUpdating || isCreating

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm()

  const profileNotFound = isError && error?.response?.data?.code === "CUSTOMER_PROFILE_NOT_FOUND"
  const formIsEditing = profileNotFound || isEditing

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

  useEffect(() => {
    if (!profileNotFound) {
      return
    }

    reset({
      id: "",
      keycloakUserId: "",
      username: "",
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
    })

  }, [profileNotFound, reset])

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
    const profileData = {
      firstName: formData.firstName.trim(),
      lastName: formData.lastName.trim(),
      phone: formData.phone.trim(),
    }

    try {
      if (profileNotFound) {
        await createCustomer(profileData)
        showSuccess("Profile created successfully.")
      } else {
        await updateCustomer(profileData)
        showSuccess("Profile updated successfully.")
      }

      setIsEditing(false)
    } catch (requestError) {
      showError(requestError, "Unable to save your profile.")
    }
  }

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading profile...</div>
  }

  if (isError && !profileNotFound) {
    return <div>{error?.message ?? "Unable to load profile."}</div>
  }

  if (!customer && !profileNotFound) {
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

          {!formIsEditing && (
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
            readOnly={!formIsEditing}
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
            readOnly={!formIsEditing}
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
            readOnly
            register={register("email")}
          />

          <ProfileField
            label="Phone"
            type="tel"
            readOnly={!formIsEditing}
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

        {formIsEditing && (
          <div className="mt-8 flex justify-end gap-3">
            {!profileNotFound && (
              <button
                type="button"
                onClick={cancelEditing}
                disabled={isSaving}
                className="rounded-md border px-5 py-2 hover:bg-accent disabled:opacity-50">
                Cancel
              </button>
            )}

            <button
              type="submit"
              disabled={isSaving}
              className="rounded-md bg-primary px-5 py-2 text-primary-foreground hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50">
              {isSaving ? "Saving..." : profileNotFound ? "Create Profile" : "Save Changes"}
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
