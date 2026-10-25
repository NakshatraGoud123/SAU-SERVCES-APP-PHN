import { supabase } from './supabase';

export type BookingInsertPayload = {
  user_id: string;
  user_name: string;
  user_phone: string;
  user_address: string;
  service_id: string;
  category: string;
  subcategory: string;
  scheduled_date: string;
  scheduled_time: string;
  time_slot: string;
  booking_date: string;
  status: string;
  provider_id: string | null;
  payment_method: string;
  payment_status: string;
  total_amount: number;
};

export async function createServiceBooking(payload: BookingInsertPayload): Promise<string> {
  const { data, error } = await supabase
    .from('bookings')
    .insert(payload)
    .select('id')
    .single();

  if (error) {
    console.error('Failed to create service booking:', error.message);
    throw error;
  }

  return data.id;
}
