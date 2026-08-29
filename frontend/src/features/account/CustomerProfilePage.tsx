import React, { useState } from 'react';
import { User, MapPin, Shield, Check, Plus, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';

export const CustomerProfilePage: React.FC = () => {
  const [firstName, setFirstName] = useState('Sarah');
  const [lastName, setLastName] = useState('Connor');
  const [email] = useState('buyer@example.com');
  const [phone, setPhone] = useState('+1 (555) 987-6543');
  const [currency, setCurrency] = useState('USD');
  const [savedSuccess, setSavedSuccess] = useState(false);

  const [addresses, setAddresses] = useState([
    {
      id: 'addr-1',
      title: 'Home',
      recipient: 'Sarah Connor',
      phone: '+1 (555) 987-6543',
      street: '742 Evergreen Terrace, Apt 4B',
      city: 'San Francisco',
      state: 'CA',
      zip: '94102',
      isDefault: true,
    },
  ]);

  const [addAddressModalOpen, setAddAddressModalOpen] = useState(false);
  const [newTitle, setNewTitle] = useState('Office');
  const [newRecipient, setNewRecipient] = useState('Sarah Connor');
  const [newPhone, setNewPhone] = useState('+1 (555) 987-6543');
  const [newStreet, setNewStreet] = useState('');
  const [newCity, setNewCity] = useState('');
  const [newState, setNewState] = useState('CA');
  const [newZip, setNewZip] = useState('');

  const handleProfileSave = (e: React.FormEvent) => {
    e.preventDefault();
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  const handleAddAddress = (e: React.FormEvent) => {
    e.preventDefault();
    setAddresses((prev) => [
      ...prev,
      {
        id: `addr-${Date.now()}`,
        title: newTitle,
        recipient: newRecipient,
        phone: newPhone,
        street: newStreet,
        city: newCity,
        state: newState,
        zip: newZip,
        isDefault: false,
      },
    ]);
    setAddAddressModalOpen(false);
    setNewStreet('');
    setNewCity('');
    setNewZip('');
  };

  const handleDeleteAddress = (id: string) => {
    setAddresses((prev) => prev.filter((a) => a.id !== id));
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Account Settings & Addresses</h1>
        <p className="text-gray-500 text-sm mt-1">Manage your identity credentials, multi-address book, and currency preferences.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Personal Profile Info */}
        <div className="lg:col-span-2 space-y-8">
          <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
            <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
              <User className="w-5 h-5 text-primary-600" /> Personal Profile
            </h3>

            {savedSuccess && (
              <div className="mb-6 p-4 bg-green-50 border border-green-200 text-green-800 rounded-xl text-sm font-medium flex items-center gap-2">
                <Check className="w-4 h-4 text-green-600" /> Profile preferences updated successfully!
              </div>
            )}

            <form onSubmit={handleProfileSave} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">First Name</label>
                  <Input value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Last Name</label>
                  <Input value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Email Address</label>
                <Input value={email} disabled className="bg-gray-50 text-gray-500 cursor-not-allowed" />
                <span className="text-xs text-gray-400 mt-1 block">Contact support to modify verified primary login email.</span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Phone Number</label>
                  <Input value={phone} onChange={(e) => setPhone(e.target.value)} />
                </div>
                <div>
                  <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Default Currency</label>
                  <select
                    value={currency}
                    onChange={(e) => setCurrency(e.target.value)}
                    className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
                  >
                    <option value="USD">USD ($) - United States Dollar</option>
                    <option value="EUR">EUR (€) - Euro</option>
                    <option value="GBP">GBP (£) - British Pound</option>
                    <option value="CAD">CAD ($) - Canadian Dollar</option>
                  </select>
                </div>
              </div>

              <div className="pt-4 border-t flex justify-end">
                <Button type="submit">Save Changes</Button>
              </div>
            </form>
          </div>

          {/* Address Book */}
          <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <MapPin className="w-5 h-5 text-primary-600" /> Address Book ({addresses.length})
              </h3>
              <Button size="sm" onClick={() => setAddAddressModalOpen(true)}>
                <Plus className="w-4 h-4 mr-1.5" /> Add Address
              </Button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {addresses.map((addr) => (
                <div key={addr.id} className="p-4 rounded-xl border border-gray-200 bg-gray-50/50 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="font-semibold text-sm text-gray-900">{addr.title}</span>
                      {addr.isDefault && <Badge variant="info">Default</Badge>}
                    </div>
                    <p className="text-xs text-gray-700 font-medium">{addr.recipient}</p>
                    <p className="text-xs text-gray-500 mt-1">{addr.street}</p>
                    <p className="text-xs text-gray-500">{addr.city}, {addr.state} {addr.zip}</p>
                    <p className="text-xs text-gray-500 mt-1">{addr.phone}</p>
                  </div>

                  {!addr.isDefault && (
                    <div className="mt-4 pt-3 border-t border-gray-200 flex justify-end">
                      <Button size="sm" variant="ghost" onClick={() => handleDeleteAddress(addr.id)} className="text-red-500 hover:text-red-600 p-1 h-auto">
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Security & Account Tier Status */}
        <div className="space-y-6">
          <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
            <h3 className="text-base font-bold text-gray-900 mb-4 flex items-center gap-2">
              <Shield className="w-5 h-5 text-green-600" /> Security & Protection
            </h3>
            <div className="space-y-3 text-sm">
              <div className="flex items-center justify-between py-2 border-b border-gray-100">
                <span className="text-gray-600">Email Verification</span>
                <Badge variant="success">Verified</Badge>
              </div>
              <div className="flex items-center justify-between py-2 border-b border-gray-100">
                <span className="text-gray-600">Buyer Protection</span>
                <span className="text-xs font-semibold text-green-600">Active ($100k Coverage)</span>
              </div>
              <div className="flex items-center justify-between py-2">
                <span className="text-gray-600">Two-Factor Auth</span>
                <span className="text-xs text-gray-400">Not Enabled</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Add Address Modal */}
      <Modal isOpen={addAddressModalOpen} onClose={() => setAddAddressModalOpen(false)} title="Add New Shipping Address">
        <form onSubmit={handleAddAddress} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Address Label</label>
            <Input value={newTitle} onChange={(e) => setNewTitle(e.target.value)} required placeholder="e.g. Home, Office, Beach House" />
          </div>
          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Recipient Name</label>
            <Input value={newRecipient} onChange={(e) => setNewRecipient(e.target.value)} required />
          </div>
          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Street Address</label>
            <Input value={newStreet} onChange={(e) => setNewStreet(e.target.value)} required placeholder="123 Main St, Suite 400" />
          </div>
          <div className="grid grid-cols-3 gap-2">
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">City</label>
              <Input value={newCity} onChange={(e) => setNewCity(e.target.value)} required />
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">State</label>
              <Input value={newState} onChange={(e) => setNewState(e.target.value)} required />
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Postal Code</label>
              <Input value={newZip} onChange={(e) => setNewZip(e.target.value)} required />
            </div>
          </div>
          <div>
            <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Phone Number</label>
            <Input value={newPhone} onChange={(e) => setNewPhone(e.target.value)} required />
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button type="button" variant="secondary" onClick={() => setAddAddressModalOpen(false)}>Cancel</Button>
            <Button type="submit">Save Address</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
